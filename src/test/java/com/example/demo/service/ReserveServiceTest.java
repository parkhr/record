package com.example.demo.service;

import static com.example.demo.enums.RecordStatus.REGISTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.record.RecordService;
import com.example.demo.record.entity.Records;
import com.example.demo.record.request.CreateRecordRequest;
import com.example.demo.reserve.ReserveService;
import com.example.demo.reserve.entity.Reserve;
import com.example.demo.reserve.request.CreateReserveRequest;
import com.example.demo.storage.StorageService;
import com.example.demo.storage.entity.StorageOut;
import com.example.demo.storage.request.LoanRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ReserveServiceTest {

    @Autowired
    private RecordService recordService;

    @Autowired
    private ReserveService reserveService;

    @Autowired
    private StorageService storageService;

    @DisplayName("예약 성공")
    @Test
    void reserve() {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus(REGISTER);
        request.setIsPublic(true);

        Records record = recordService.createRecord(request);

        LoanRequest request2 = new LoanRequest();
        request2.setRecordId(record.getId());
        request2.setUserId(1L);

        //when
        StorageOut storageOut = storageService.loan(request2);

        CreateReserveRequest request3 = new CreateReserveRequest();
        request3.setRecordId(record.getId());
        request3.setUserId(1L);

        //when
        Reserve reserve = reserveService.reserve(request3);

        //then
        assertThat(reserve.getRecordId()).isEqualTo(record.getId());
        assertThat(reserve.getUserId()).isEqualTo(1L);
        assertNotNull(reserve.getCreatedAt());
    }

    @DisplayName("예약 실패 (중복 예약할 경우)")
    @Test
    void reserve_fail() {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus(REGISTER);
        request.setIsPublic(true);

        Records record = recordService.createRecord(request);

        LoanRequest request2 = new LoanRequest();
        request2.setRecordId(record.getId());
        request2.setUserId(1L);

        //when
        StorageOut storageOut = storageService.loan(request2);

        CreateReserveRequest request3 = new CreateReserveRequest();
        request3.setRecordId(record.getId());
        request3.setUserId(1L);

        //when //then
        reserveService.reserve(request3);

        assertThatThrownBy(() -> reserveService.reserve(request3)).isInstanceOf(ApplicationException.class).hasMessage("이미 예약한 기록물입니다.");
    }

    @DisplayName("예약 실패 (대출할 수 있는 기록물)")
    @Test
    void reserve_fail2() {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus(REGISTER);
        request.setIsPublic(true);

        Records record = recordService.createRecord(request);

        CreateReserveRequest request3 = new CreateReserveRequest();
        request3.setRecordId(record.getId());
        request3.setUserId(1L);

        //when //then
        assertThatThrownBy(() -> reserveService.reserve(request3)).isInstanceOf(ApplicationException.class).hasMessage("현재 대출할 수 있는 기록물입니다.");
    }
}