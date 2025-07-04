package com.example.demo.service;

import static com.example.demo.common.ErrorMessage.RECORD_ALREADY_ON_LOAN;
import static com.example.demo.common.ErrorMessage.RECORD_NOT_AVAILABLE_FOR_LOAN;
import static com.example.demo.common.ErrorMessage.RECORD_NOT_FOUND;
import static com.example.demo.enums.RecordStatus.REGISTER;
import static com.example.demo.enums.RecordStatus.TEMP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.entity.Records;
import com.example.demo.entity.StorageOut;
import com.example.demo.request.CreateRecordRequest;
import com.example.demo.request.LoanRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class StorageServiceTest {

    @Autowired
    private RecordService recordService;

    @Autowired
    private StorageService storageService;

    @DisplayName("대출 성공")
    @Test
    void loan() {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus(REGISTER);
        request.setVisibility("공개");

        Records record = recordService.createRecord(request);

        LoanRequest request2 = new LoanRequest();
        request2.setRecordId(record.getId());

        //when
        StorageOut storageOut = storageService.loan(request2);

        //then
        assertThat(storageOut.getRecordId()).isEqualTo(record.getId());
        assertNotNull(storageOut.getDueDate());
        assertNotNull(storageOut.getCreatedAt());
    }

    @DisplayName("대출 실패 (기록물이 없는 경우)")
    @Test
    void loan_fail1() {
        //given

        LoanRequest request2 = new LoanRequest();
        request2.setRecordId(0L);

        //when
        //then
        assertThatThrownBy(() -> storageService.loan(request2)).isInstanceOf(ApplicationException.class).hasMessage(RECORD_NOT_FOUND);
    }

    @DisplayName("대출 실패 (삭제된 기록물)")
    @Test
    void loan_fail2() {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus(REGISTER);
        request.setVisibility("공개");

        Records record = recordService.createRecord(request);
        recordService.deleteRecord(record.getId()); // 삭제

        LoanRequest request2 = new LoanRequest();
        request2.setRecordId(record.getId());

        //when
        //then
        assertThatThrownBy(() -> storageService.loan(request2)).isInstanceOf(ApplicationException.class).hasMessage(RECORD_NOT_AVAILABLE_FOR_LOAN);
    }

    @DisplayName("대출 실패 (임시 기록물)")
    @Test
    void loan_fail3() {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus(TEMP);
        request.setVisibility("공개");

        Records record = recordService.createRecord(request);

        LoanRequest request2 = new LoanRequest();
        request2.setRecordId(record.getId());

        //when
        //then
        assertThatThrownBy(() -> storageService.loan(request2)).isInstanceOf(ApplicationException.class).hasMessage(RECORD_NOT_AVAILABLE_FOR_LOAN);
    }

    @DisplayName("대출 실패 (비공개 기록물)")
    @Test
    void loan_fail4() {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus(REGISTER);
        request.setVisibility("비공개");

        Records record = recordService.createRecord(request);

        LoanRequest request2 = new LoanRequest();
        request2.setRecordId(record.getId());

        //when
        //then
        assertThatThrownBy(() -> storageService.loan(request2)).isInstanceOf(ApplicationException.class).hasMessage(RECORD_NOT_AVAILABLE_FOR_LOAN);
    }

    @DisplayName("대출 실패 (대출중인 기록물)")
    @Test
    void loan_fail5() {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus(REGISTER);
        request.setVisibility("공개");

        Records record = recordService.createRecord(request);

        LoanRequest request2 = new LoanRequest();
        request2.setRecordId(record.getId());

        //when
        StorageOut storageOut = storageService.loan(request2);

        //then
        assertThat(storageOut.getRecordId()).isEqualTo(record.getId());
        assertNotNull(storageOut.getDueDate());
        assertNotNull(storageOut.getCreatedAt());

        // 한번 더 대출
        assertThatThrownBy(() -> storageService.loan(request2)).isInstanceOf(ApplicationException.class).hasMessage(RECORD_ALREADY_ON_LOAN);
    }
}