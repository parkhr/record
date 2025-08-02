package com.example.demo.economy;

import static com.example.demo.common.ErrorMessage.ADMIN_NOT_FOUND;

import com.example.demo.admin.entity.Admin;
import com.example.demo.admin.repository.AdminRepository;
import com.example.demo.common.CustomUserDetails;
import com.example.demo.common.exception.ApplicationException;
import com.example.demo.common.util.UserUtil;
import com.example.demo.economy.domain.CardSmsParser;
import com.example.demo.economy.domain.CardSmsRecord;
import com.example.demo.economy.entity.Active;
import com.example.demo.economy.entity.Spend;
import com.example.demo.economy.entity.Wallet;
import com.example.demo.economy.repository.ActiveRepository;
import com.example.demo.economy.repository.SearchSpendResponse;
import com.example.demo.economy.repository.SpendRepository;
import com.example.demo.economy.repository.WalletRepository;
import com.example.demo.economy.request.CancelMinusAmountRequest;
import com.example.demo.economy.request.CancelPlusAmountRequest;
import com.example.demo.economy.request.CreateActiveRequest;
import com.example.demo.economy.request.CreateSpendRequest;
//import com.example.demo.economy.request.UpdateSpendRequest;
import com.example.demo.economy.request.MinusAmountRequest;
import com.example.demo.economy.request.PlusAmountRequest;
import com.example.demo.economy.request.SearchActiveRequest;
import com.example.demo.economy.request.SearchSpendRequest;
import com.example.demo.economy.response.SearchActiveResponse;
import com.example.demo.economy.response.WalletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EconomyService {

    private final AdminRepository adminRepository;
    private final WalletRepository walletRepository;
    private final SpendRepository spendRepository;
    private final ActiveRepository activeRepository;

    public Spend createSpend(CreateSpendRequest request) {
        CardSmsRecord cardSmsRecord = CardSmsParser.parse(request.getMessage());
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));

        return spendRepository.save(Spend.createSpend(cardSmsRecord, userDetails.getId()));
    }

    public Page<SearchSpendResponse> findSpends(SearchSpendRequest request, Pageable pageable) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));

        return spendRepository.findSpends(request, userDetails.getId(), pageable);
    }

    @Transactional
    public void minusAmount(MinusAmountRequest request) {
        Spend spend = spendRepository.findById(request.getSpendId()).orElseThrow(() -> new ApplicationException("지출내역을 찾을 수 없습니다."));

        if (spend.isDeducted()) {
            throw new ApplicationException("이미 차감된 지출내역 입니다.");
        }

        if (spend.isDeleted()) {
            throw new ApplicationException("삭제된 지출내역 입니다.");
        }

        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

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

        //TODO 유저 활동 로깅
    }

    @Transactional
    public void cancelMinusAmount(CancelMinusAmountRequest request) {
        Spend spend = spendRepository.findById(request.getSpendId()).orElseThrow(() -> new ApplicationException("지출내역을 찾을 수 없습니다."));

        if (!spend.isDeducted()) {
            throw new ApplicationException("이미 차감 취소된 지출내역 입니다.");
        }

        if (spend.isDeleted()) {
            throw new ApplicationException("삭제된 지출내역 입니다.");
        }

        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

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

        //TODO 유저 활동 로깅
    }

    @Transactional
    public void deleteSpend(Long spendId) {
        Spend spend = spendRepository.findById(spendId).orElseThrow(() -> new ApplicationException("지출내역을 찾을 수 없습니다."));

        if (spend.isDeleted()) {
            throw new ApplicationException("삭제된 지출내역 입니다.");
        }

        spend.delete();
        spendRepository.save(spend);
    }

    @Transactional
    public Active createActive(CreateActiveRequest request) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Active active = Active.createActive(request, userDetails.getId());

        return activeRepository.save(active);
    }

    @Transactional
    public void deleteActive(Long activeId) {
        Active active = activeRepository.findById(activeId).orElseThrow(() -> new ApplicationException("활동내역을 찾을 수 없습니다."));

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

    public void plusAmount(PlusAmountRequest request) {
        Active active = activeRepository.findById(request.getActiveId()).orElseThrow(() -> new ApplicationException("활동내역을 찾을 수 없습니다."));

        if (active.isSaved()) {
            throw new ApplicationException("이미 적립된 활동내역 입니다.");
        }

        if (active.isDeleted()) {
            throw new ApplicationException("삭제된 활동내역 입니다.");
        }

        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

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
    }

    public void cancelPlusAmount(CancelPlusAmountRequest request) {
        Active active = activeRepository.findById(request.getActiveId()).orElseThrow(() -> new ApplicationException("활동내역을 찾을 수 없습니다."));

        if (!active.isSaved()) {
            throw new ApplicationException("이미 적립 취소된 활동내역 입니다.");
        }

        if (active.isDeleted()) {
            throw new ApplicationException("삭제된 활동내역 입니다.");
        }

        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

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
}
