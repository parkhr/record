package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.economy.EconomyService;
import com.example.demo.economy.entity.Spend;
import com.example.demo.economy.request.CreateSpendRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class EconomyServiceTest {

    @Autowired
    private EconomyService economyService;

    @DisplayName("지출 등록")
    @Test
    void createSpend() {
        //given
        CreateSpendRequest request = new CreateSpendRequest();
        request.setMessage("[Web발신]\n"
            + "하나1*8*승인 박*렬 5,640원 일시불 07/29 03:28 카카오_택시_0 누적194,686원");

        //when
        Spend spend = economyService.createSpend(request);

        //then
        assertThat(spend.getAmount()).isEqualTo(5640);
        assertThat(spend.getPlace()).isEqualTo("카카오_택시_0");
        assertThat(spend.isDeducted()).isFalse();
    }
}