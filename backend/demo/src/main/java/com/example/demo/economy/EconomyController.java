package com.example.demo.economy;

import com.example.demo.economy.request.CreateSpendRequest;
import com.example.demo.economy.request.MinusAmountRequest;
import com.example.demo.economy.request.SearchSpendRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
//    @PreAuthorize("hasRole('WRITE_RECORD')")
    public ResponseEntity<Object> createSpend(@RequestBody CreateSpendRequest request) {

        economyService.createSpend(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/spend/search")
    public ResponseEntity<Object> findSpends(SearchSpendRequest request, Pageable pageable) {
        return ResponseEntity.ok(economyService.findSpends(request, pageable));
    }

    // 지출내역 수정
//    @PutMapping
//    @PreAuthorize("hasRole('UPDATE_RECORD')")
//    public ResponseEntity<Object> updateSpend(@RequestBody UpdateSpendRequest request) {
//
//        economyService.updateSpend(request);
//        return ResponseEntity.ok().build();
//    }

    // 지출내역 삭제
//    @DeleteMapping("/{recordId}")
//    @PreAuthorize("hasRole('DELETE_RECORD')")
//    public ResponseEntity<Object> deleteRecord(@PathVariable("spendId") Long spendId) {
//
//        economyService.deleteSpend(spendId);
//        return ResponseEntity.ok().build();
//    }

    // 금액차감
    @PostMapping("/minus")
//    @PreAuthorize("hasRole('WRITE_RECORD')")
    public ResponseEntity<Object> minusAmount(@RequestBody MinusAmountRequest request) {

        economyService.minusAmount(request);
        return ResponseEntity.ok().build();
    }

    // 금액차감 취소

    // 활동내역 생성

    // 활동내역 삭제

    // 수입적립

    // 수입적립 취소
}
