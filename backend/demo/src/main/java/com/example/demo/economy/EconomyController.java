package com.example.demo.economy;

import com.example.demo.economy.request.CancelMinusAmountRequest;
import com.example.demo.economy.request.CancelPlusAmountRequest;
import com.example.demo.economy.request.CreateActiveRequest;
import com.example.demo.economy.request.CreateSpendRequest;
import com.example.demo.economy.request.MinusAmountRequest;
import com.example.demo.economy.request.PlusAmountRequest;
import com.example.demo.economy.request.SearchActiveRequest;
import com.example.demo.economy.request.SearchSpendRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/economy")
public class EconomyController {

    private final EconomyService economyService;

    // 지출내역 생성
    @PostMapping("/spend")
    public ResponseEntity<Object> createSpend(@RequestBody CreateSpendRequest request) {

        economyService.createSpend(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/spend/search")
    public ResponseEntity<Object> findSpends(SearchSpendRequest request, Pageable pageable) {
        return ResponseEntity.ok(economyService.findSpends(request, pageable));
    }

    // 지출내역 삭제
    @DeleteMapping("/spend/{spendId}")
    public ResponseEntity<Object> deleteSpend(@PathVariable("spendId") Long spendId) {

        economyService.deleteSpend(spendId);
        return ResponseEntity.ok().build();
    }

    // 금액차감
    @PostMapping("/minus")
    public ResponseEntity<Object> minusAmount(@RequestBody MinusAmountRequest request) {

        economyService.minusAmount(request);
        return ResponseEntity.ok().build();
    }

    // 금액차감 취소
    @PostMapping("/cancel-minus")
    public ResponseEntity<Object> cancelMinusAmount(@RequestBody CancelMinusAmountRequest request) {

        economyService.cancelMinusAmount(request);
        return ResponseEntity.ok().build();
    }

    // 활동내역 생성
    @PostMapping("/active")
    public ResponseEntity<Object> createActive(@RequestBody CreateActiveRequest request) {

        economyService.createActive(request);
        return ResponseEntity.ok().build();
    }

    // 활동내역 삭제
    @DeleteMapping("/active/{activeId}")
    public ResponseEntity<Object> deleteActive(@PathVariable("activeId") Long activeId) {

        economyService.deleteActive(activeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/active/search")
    public ResponseEntity<Object> findActives(SearchActiveRequest request, Pageable pageable) {
        return ResponseEntity.ok(economyService.findActives(request, pageable));
    }

    // 수입적립
    @PostMapping("/plus")
    public ResponseEntity<Object> plusAmount(@RequestBody PlusAmountRequest request) {

        economyService.plusAmount(request);
        return ResponseEntity.ok().build();
    }

    // 수입적립 취소
    @PostMapping("/cancel-plus")
    public ResponseEntity<Object> cancelPlusAmount(@RequestBody CancelPlusAmountRequest request) {

        economyService.cancelPlusAmount(request);
        return ResponseEntity.ok().build();
    }

    // 내지갑 불러오기
    @GetMapping("/wallet")
    public ResponseEntity<Object> findWallet() {
        return ResponseEntity.ok(economyService.findMyWallet());
    }

    // 최근 리스트
    @GetMapping("/dashboard/recent")
    public ResponseEntity<Object> recent() {
        return ResponseEntity.ok(economyService.recent());
    }

    // 이번주 수입
    @GetMapping("/dashboard/active")
    public ResponseEntity<Object> thisWeekActive() {
        return ResponseEntity.ok(economyService.thisWeekActive());
    }

    // 이번주 지출
    @GetMapping("/dashboard/spend")
    public ResponseEntity<Object> thisWeekSpend() {
        return ResponseEntity.ok(economyService.thisWeekSpend());
    }
}
