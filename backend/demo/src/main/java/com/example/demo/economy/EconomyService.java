package com.example.demo.economy;

import static com.example.demo.common.ErrorMessage.ADMIN_NOT_FOUND;

import com.example.demo.admin.entity.Admin;
import com.example.demo.admin.repository.AdminRepository;
import com.example.demo.common.CustomUserDetails;
import com.example.demo.common.exception.ApplicationException;
import com.example.demo.common.util.UserUtil;
import com.example.demo.economy.domain.CardSmsParser;
import com.example.demo.economy.domain.CardSmsRecord;
import com.example.demo.economy.entity.Spend;
import com.example.demo.economy.entity.Wallet;
import com.example.demo.economy.repository.SearchSpendResponse;
import com.example.demo.economy.repository.SpendRepository;
import com.example.demo.economy.repository.WalletRepository;
import com.example.demo.economy.request.CreateSpendRequest;
//import com.example.demo.economy.request.UpdateSpendRequest;
import com.example.demo.economy.request.MinusAmountRequest;
import com.example.demo.economy.request.SearchSpendRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EconomyService {

    private final AdminRepository adminRepository;
    private final WalletRepository walletRepository;
    private final SpendRepository spendRepository;

    public Spend createSpend(CreateSpendRequest request) {
        CardSmsRecord cardSmsRecord = CardSmsParser.parse(request.getMessage());

        return spendRepository.save(Spend.createSpend(cardSmsRecord));
    }

    public Page<SearchSpendResponse> findSpends(SearchSpendRequest request, Pageable pageable) {
        return spendRepository.findSpends(request, pageable);
    }

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

        //TODO 차감처리

        //TODO 유저 활동 로깅
    }

//    public void updateSpend(UpdateSpendRequest request) {
//
//    }

//    public void deleteSpend(Long spendId) {
//
//    }
}
