package com.example.demo.economy;

import static com.example.demo.common.ErrorMessage.ADMIN_NOT_FOUND;

import com.example.demo.admin.entity.Admin;
import com.example.demo.admin.repository.AdminRepository;
import com.example.demo.common.CustomUserDetails;
import com.example.demo.common.exception.ApplicationException;
import com.example.demo.common.util.DateUtil;
import com.example.demo.common.util.UserUtil;
import com.example.demo.economy.domain.CardSmsParser;
import com.example.demo.economy.domain.CardSmsRecord;
import com.example.demo.economy.domain.PayPolicy;
import com.example.demo.economy.entity.Active;
import com.example.demo.economy.entity.Spend;
import com.example.demo.economy.entity.Wallet;
import com.example.demo.economy.entity.WalletLog;
import com.example.demo.economy.repository.ActiveRepository;
import com.example.demo.economy.repository.SearchSpendResponse;
import com.example.demo.economy.repository.SpendRepository;
import com.example.demo.economy.repository.WalletLogRepository;
import com.example.demo.economy.repository.WalletRepository;
import com.example.demo.economy.request.CancelMinusAmountRequest;
import com.example.demo.economy.request.CancelPlusAmountRequest;
import com.example.demo.economy.request.CreateActiveRequest;
import com.example.demo.economy.request.CreateSpendRequest;
import com.example.demo.economy.request.MinusAmountRequest;
import com.example.demo.economy.request.PlusAmountRequest;
import com.example.demo.economy.request.SearchActiveRequest;
import com.example.demo.economy.request.SearchSpendRequest;
import com.example.demo.economy.response.DashboardActiveMonthResponse;
import com.example.demo.economy.response.DashboardActiveResponse;
import com.example.demo.economy.response.DashboardBreakEvenTimeResponse;
import com.example.demo.economy.response.DashboardRecentResponse;
import com.example.demo.economy.response.DashboardSpendMonthResponse;
import com.example.demo.economy.response.DashboardSpendResponse;
import com.example.demo.economy.response.SearchActiveResponse;
import com.example.demo.economy.response.WalletResponse;
import com.example.demo.push.PushMessage;
import com.example.demo.push.PushSendResolver;
import com.example.demo.push.PushSender;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EconomyService {

    private final AdminRepository adminRepository;
    private final WalletRepository walletRepository;
    private final WalletLogRepository walletLogRepository;
    private final SpendRepository spendRepository;
    private final ActiveRepository activeRepository;

    private final PushSendResolver pushSendResolver;

    @Value("${push.api.base-url}")
    private String baseUrl;

    @Transactional
    public Spend createSpend(CreateSpendRequest request) {
        CardSmsRecord cardSmsRecord = CardSmsParser.parse(request.getMessage());
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자입니다.");
        }

        return spendRepository.save(Spend.createSpend(cardSmsRecord, admin.getId()));
    }

    public Page<SearchSpendResponse> findSpends(SearchSpendRequest request, Pageable pageable) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));

        return spendRepository.findSpends(request, userDetails.getId(), pageable);
    }

    @Transactional
    public void minusAmount(MinusAmountRequest request) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));
        Spend spend = spendRepository.findById(request.getSpendId()).orElseThrow(() -> new ApplicationException("지출내역을 찾을 수 없습니다."));

        if (spend.getAdminId() != admin.getId()) {
            throw new ApplicationException("권한이 없습니다.");
        }

        if (spend.isDeducted()) {
            throw new ApplicationException("이미 차감된 지출내역 입니다.");
        }

        if (spend.isDeleted()) {
            throw new ApplicationException("삭제된 지출내역 입니다.");
        }

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자 입니다.");
        }

        Wallet wallet = walletRepository.findByAdminId(userDetails.getId()).orElseThrow(() -> new ApplicationException("해당 관리자의 지갑을 찾을 수 없습니다."));

        if (wallet.isDeleted()) {
            throw new ApplicationException("삭제된 지갑 입니다.");
        }

        spend.deduct();
        spendRepository.save(spend);

        wallet.minusAmount(spend);
        walletRepository.save(wallet);

        walletLogRepository.save(WalletLog.createWalletLog("SPEND", admin.getId(), spend.getAmount() * -1));

        if (wallet.getAmount() < 0) {
            PushSender pushAppSender = pushSendResolver.resolve();

            pushAppSender.send(PushMessage.builder().title("현재 잔액이 부족해요").body("가벼운 활동 하나로도 잔액을 다시 채울 수 있어요. 화이팅!").build());
        }
    }

    @Transactional
    public void cancelMinusAmount(CancelMinusAmountRequest request) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));
        Spend spend = spendRepository.findById(request.getSpendId()).orElseThrow(() -> new ApplicationException("지출내역을 찾을 수 없습니다."));

        if (spend.getAdminId() != admin.getId()) {
            throw new ApplicationException("권한이 없습니다.");
        }

        if (!spend.isDeducted()) {
            throw new ApplicationException("이미 차감 취소된 지출내역 입니다.");
        }

        if (spend.isDeleted()) {
            throw new ApplicationException("삭제된 지출내역 입니다.");
        }

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자 입니다.");
        }

        Wallet wallet = walletRepository.findByAdminId(userDetails.getId()).orElseThrow(() -> new ApplicationException("해당 관리자의 지갑을 찾을 수 없습니다."));

        if (wallet.isDeleted()) {
            throw new ApplicationException("삭제된 지갑 입니다.");
        }

        spend.cancelDeduct();
        spendRepository.save(spend);

        wallet.cancelMinusAmount(spend);
        walletRepository.save(wallet);

        walletLogRepository.save(WalletLog.createWalletLog("CANCEL_SPEND", admin.getId(), spend.getAmount()));
    }

    @Transactional
    public void deleteSpend(Long spendId) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));
        Spend spend = spendRepository.findById(spendId).orElseThrow(() -> new ApplicationException("지출내역을 찾을 수 없습니다."));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자입니다.");
        }

        if (spend.getAdminId() != admin.getId()) {
            throw new ApplicationException("권한이 없습니다.");
        }

        if (spend.isDeleted()) {
            throw new ApplicationException("삭제된 지출내역 입니다.");
        }

        spend.delete();
        spendRepository.save(spend);
    }

    @Transactional
    public Active createActive(CreateActiveRequest request) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));
        Active active = Active.createActive(request, userDetails.getId());

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자입니다.");
        }

        return activeRepository.save(active);
    }

    @Transactional
    public void deleteActive(Long activeId) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));
        Active active = activeRepository.findById(activeId).orElseThrow(() -> new ApplicationException("활동내역을 찾을 수 없습니다."));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자입니다.");
        }

        if (active.getAdminId() != admin.getId()) {
            throw new ApplicationException("권한이 없습니다.");
        }

        if (active.isDeleted()) {
            throw new ApplicationException("삭제된 활동내역 입니다.");
        }

        active.delete();
        activeRepository.save(active);
    }

    public Page<SearchActiveResponse> findActives(SearchActiveRequest request, Pageable pageable) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));

        return activeRepository.findActives(request, userDetails.getId(), pageable);
    }

    @Transactional
    public void plusAmount(PlusAmountRequest request) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));
        Active active = activeRepository.findById(request.getActiveId()).orElseThrow(() -> new ApplicationException("활동내역을 찾을 수 없습니다."));

        if (active.getAdminId() != admin.getId()) {
            throw new ApplicationException("권한이 없습니다.");
        }

        if (active.isSaved()) {
            throw new ApplicationException("이미 적립된 활동내역 입니다.");
        }

        if (active.isDeleted()) {
            throw new ApplicationException("삭제된 활동내역 입니다.");
        }

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자 입니다.");
        }

        Wallet wallet = walletRepository.findByAdminId(userDetails.getId()).orElseThrow(() -> new ApplicationException("해당 관리자의 지갑을 찾을 수 없습니다."));

        if (wallet.isDeleted()) {
            throw new ApplicationException("삭제된 지갑 입니다.");
        }

        active.save();
        activeRepository.save(active);

        wallet.plusAmount(active);
        walletRepository.save(wallet);

        walletLogRepository.save(WalletLog.createWalletLog("ACTIVE", admin.getId(), active.getAmount()));
    }

    @Transactional
    public void cancelPlusAmount(CancelPlusAmountRequest request) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));
        Active active = activeRepository.findById(request.getActiveId()).orElseThrow(() -> new ApplicationException("활동내역을 찾을 수 없습니다."));

        if (active.getAdminId() != admin.getId()) {
            throw new ApplicationException("권한이 없습니다.");
        }

        if (!active.isSaved()) {
            throw new ApplicationException("이미 적립 취소된 활동내역 입니다.");
        }

        if (active.isDeleted()) {
            throw new ApplicationException("삭제된 활동내역 입니다.");
        }

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자 입니다.");
        }

        Wallet wallet = walletRepository.findByAdminId(userDetails.getId()).orElseThrow(() -> new ApplicationException("해당 관리자의 지갑을 찾을 수 없습니다."));

        if (wallet.isDeleted()) {
            throw new ApplicationException("삭제된 지갑 입니다.");
        }

        active.cancelSave();
        activeRepository.save(active);

        wallet.cancelPlusAmount(active);
        walletRepository.save(wallet);

        walletLogRepository.save(WalletLog.createWalletLog("CANCEL_ACTIVE", admin.getId(), active.getAmount() * -1));
    }

    public WalletResponse findMyWallet() {

        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자 입니다.");
        }

        Wallet wallet = walletRepository.findByAdminId(userDetails.getId()).orElseThrow(() -> new ApplicationException("해당 관리자의 지갑을 찾을 수 없습니다."));

        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setAmount(wallet.getAmount());
        walletResponse.setLastSaved(1500);
        walletResponse.setLastSpend(2300);

        return walletResponse;
    }

    public List<DashboardRecentResponse> recent() {

        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자 입니다.");
        }

        List<WalletLog> top5WalletLogs = walletLogRepository.findTop5ByAdminIdOrderByCreatedAtDesc(admin.getId());

        List<DashboardRecentResponse> result = new ArrayList<>();

        for (WalletLog walletLog : top5WalletLogs) {
            DashboardRecentResponse item = new DashboardRecentResponse();
            item.setAmount(walletLog.getAmount());
            item.setStatus(walletLog.getStatus());
            item.setDate(walletLog.getCreatedAt());
            result.add(item);
        }

        return result;
    }

    public DashboardActiveResponse thisWeekActive() {

        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자 입니다.");
        }

        List<LocalDateTime> weekRange = DateUtil.getWeekRange(LocalDate.now());
        List<Active> actives = activeRepository.findByAdminIdAndSavedTrueAndCreatedAtBetween(admin, weekRange.get(0), weekRange.get(1));

        // 일별 집계도 포함하고 싶다면:
        Map<DayOfWeek, Integer> dailyMap = Arrays.stream(DayOfWeek.values()).collect(Collectors.toMap(d -> d, d -> 0));

        for (Active active : actives) {
            DayOfWeek dow = active.getCreatedAt().getDayOfWeek();
            dailyMap.put(dow, dailyMap.get(dow) + active.getAmount());
        }

        DashboardActiveResponse response = new DashboardActiveResponse();
        response.setAmounts(Arrays.stream(DayOfWeek.values()).sorted(Comparator.comparingInt(DayOfWeek::getValue)) // 월요일=1 ~ 일요일=7
            .map(dow -> dailyMap.getOrDefault(dow, 0)).toList());

        return response;
    }

    public DashboardActiveResponse lastWeekActive() {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자 입니다.");
        }

        List<LocalDateTime> weekRange = DateUtil.getWeekRange(LocalDate.now().minusWeeks(1));
        List<Active> actives = activeRepository.findByAdminIdAndSavedTrueAndCreatedAtBetween(admin, weekRange.get(0), weekRange.get(1));

        // 일별 집계도 포함하고 싶다면:
        Map<DayOfWeek, Integer> dailyMap = Arrays.stream(DayOfWeek.values()).collect(Collectors.toMap(d -> d, d -> 0));

        for (Active active : actives) {
            DayOfWeek dow = active.getCreatedAt().getDayOfWeek();
            dailyMap.put(dow, dailyMap.get(dow) + active.getAmount());
        }

        DashboardActiveResponse response = new DashboardActiveResponse();
        response.setAmounts(Arrays.stream(DayOfWeek.values()).sorted(Comparator.comparingInt(DayOfWeek::getValue)) // 월요일=1 ~ 일요일=7
            .map(dow -> dailyMap.getOrDefault(dow, 0)).toList());

        return response;
    }

    public DashboardSpendResponse thisWeekSpend() {

        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자 입니다.");
        }

        List<LocalDateTime> weekRange = DateUtil.getWeekRange(LocalDate.now());
        List<Spend> spends = spendRepository.findByAdminIdAndDeductedTrueAndSpendAtBetween(admin, weekRange.get(0), weekRange.get(1));

        // 일별 집계도 포함하고 싶다면:
        Map<DayOfWeek, Integer> dailyMap = Arrays.stream(DayOfWeek.values()).collect(Collectors.toMap(d -> d, d -> 0));

        for (Spend spend : spends) {
            DayOfWeek dow = spend.getSpendAt().getDayOfWeek();
            dailyMap.put(dow, dailyMap.get(dow) + spend.getAmount());
        }

        DashboardSpendResponse response = new DashboardSpendResponse();
        response.setAmounts(Arrays.stream(DayOfWeek.values()).sorted(Comparator.comparingInt(DayOfWeek::getValue)) // 월요일=1 ~ 일요일=7
            .map(dow -> dailyMap.getOrDefault(dow, 0)).toList());

        return response;
    }

    public DashboardSpendResponse lastWeekSpend() {

        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자 입니다.");
        }

        List<LocalDateTime> weekRange = DateUtil.getWeekRange(LocalDate.now().minusWeeks(1));
        List<Spend> spends = spendRepository.findByAdminIdAndDeductedTrueAndSpendAtBetween(admin, weekRange.get(0), weekRange.get(1));

        // 일별 집계도 포함하고 싶다면:
        Map<DayOfWeek, Integer> dailyMap = Arrays.stream(DayOfWeek.values()).collect(Collectors.toMap(d -> d, d -> 0));

        for (Spend spend : spends) {
            DayOfWeek dow = spend.getSpendAt().getDayOfWeek();
            dailyMap.put(dow, dailyMap.get(dow) + spend.getAmount());
        }

        DashboardSpendResponse response = new DashboardSpendResponse();
        response.setAmounts(Arrays.stream(DayOfWeek.values()).sorted(Comparator.comparingInt(DayOfWeek::getValue)) // 월요일=1 ~ 일요일=7
            .map(dow -> dailyMap.getOrDefault(dow, 0)).toList());

        return response;
    }

    public DashboardSpendMonthResponse thisMonthSpend() {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자입니다.");
        }

        LocalDate today = LocalDate.now();
        List<LocalDateTime> monthRange = DateUtil.getMonthRange(today);

        List<Spend> spends = spendRepository.findByAdminIdAndDeductedTrueAndSpendAtBetween(admin, monthRange.get(0), monthRange.get(1));

        int daysInMonth = today.lengthOfMonth();
        Map<Integer, Integer> dailyMap = IntStream.rangeClosed(1, daysInMonth).boxed().collect(Collectors.toMap(day -> day, day -> 0));

        for (Spend spend : spends) {
            int dayOfMonth = spend.getSpendAt().getDayOfMonth();
            dailyMap.put(dayOfMonth, dailyMap.get(dayOfMonth) + spend.getAmount());
        }

        List<Integer> dailyAmounts = IntStream.rangeClosed(1, daysInMonth).mapToObj(d -> dailyMap.getOrDefault(d, 0)).toList();

        DashboardSpendMonthResponse response = new DashboardSpendMonthResponse();
        response.setAmounts(dailyAmounts);
        return response;
    }

    public DashboardActiveMonthResponse thisMonthActive() {

        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자입니다.");
        }

        LocalDate today = LocalDate.now();
        List<LocalDateTime> monthRange = DateUtil.getMonthRange(today);

        List<Active> actives = activeRepository.findByAdminIdAndSavedTrueAndCreatedAtBetween(admin, monthRange.get(0), monthRange.get(1));

        int daysInMonth = today.lengthOfMonth();
        Map<Integer, Integer> dailyMap = IntStream.rangeClosed(1, daysInMonth).boxed().collect(Collectors.toMap(day -> day, day -> 0));

        for (Active active : actives) {
            int dayOfMonth = active.getCreatedAt().getDayOfMonth();
            dailyMap.put(dayOfMonth, dailyMap.get(dayOfMonth) + active.getAmount());
        }

        List<Integer> dailyAmounts = IntStream.rangeClosed(1, daysInMonth).mapToObj(d -> dailyMap.getOrDefault(d, 0)).toList();

        DashboardActiveMonthResponse response = new DashboardActiveMonthResponse();
        response.setAmounts(dailyAmounts);
        return response;
    }

    public DashboardBreakEvenTimeResponse getBreakEvenTime() {

        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));
        Wallet wallet = walletRepository.findByAdminId(userDetails.getId()).orElseThrow(() -> new ApplicationException("해당 관리자의 지갑을 찾을 수 없습니다."));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자 입니다.");
        }

        if (wallet.getAmount() >= 0) {
            DashboardBreakEvenTimeResponse response = new DashboardBreakEvenTimeResponse();
            response.setHour(0);
            response.setMinutes(0);

            return response;
        }

        int requiredMinutes = (int) Math.ceil(Math.abs(wallet.getAmount()) / (double) PayPolicy.VERSION_1.getPayPerMinute());
        int hours = requiredMinutes / 60;
        int minutes = requiredMinutes % 60;

        DashboardBreakEvenTimeResponse response = new DashboardBreakEvenTimeResponse();
        response.setHour(hours);
        response.setMinutes(minutes);

        return response;
    }
}