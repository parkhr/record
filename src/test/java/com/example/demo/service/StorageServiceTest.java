package com.example.demo.service;

import static com.example.demo.common.ErrorMessage.DELAY_LIMIT_EXCEEDED;
import static com.example.demo.common.ErrorMessage.RECORD_ALREADY_ON_LOAN;
import static com.example.demo.common.ErrorMessage.RECORD_IS_NOT_ON_LOAN;
import static com.example.demo.common.ErrorMessage.RECORD_NOT_AVAILABLE_FOR_LOAN;
import static com.example.demo.common.ErrorMessage.RECORD_NOT_FOUND;
import static com.example.demo.enums.RecordStatus.REGISTER;
import static com.example.demo.enums.RecordStatus.TEMP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.record.RecordService;
import com.example.demo.record.entity.Records;
import com.example.demo.storage.entity.StorageIn;
import com.example.demo.storage.entity.StorageOut;
import com.example.demo.storage.repository.StorageOutRepository;
import com.example.demo.storage.request.CancelLoanRequest;
import com.example.demo.record.request.CreateRecordRequest;
import com.example.demo.storage.request.LoanRequest;
import com.example.demo.storage.request.ReturnDelayRequest;
import com.example.demo.storage.request.ReturnRequest;
import com.example.demo.storage.StorageService;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class StorageServiceTest {

    @Autowired
    private RecordService recordService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private StorageOutRepository storageOutRepository;

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
        request2.setUserId(1L);

        //when
        StorageOut storageOut = storageService.loan(request2);

        //then
        assertThat(storageOut.getRecordId()).isEqualTo(record.getId());
        assertNotNull(storageOut.getDueDate());
        assertNotNull(storageOut.getCreatedAt());
    }

    @DisplayName("대출 성공 (동시성 체크)")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void loan_2() throws InterruptedException {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus(REGISTER);
        request.setVisibility("공개");

        Records record = recordService.createRecord(request);

        LoanRequest request2 = new LoanRequest();
        request2.setRecordId(record.getId());
        request2.setUserId(1L);

        //when
        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(100);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        Runnable task = () -> {
            try {
                storageService.loan(request2);
                successCount.incrementAndGet();
            } catch (ApplicationException e) {
                System.out.println(e.getMessage());
                failureCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        };

        for (int i = 0; i < 100; i++) {
            executor.submit(task);
        }
        latch.await();
        executor.shutdown();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(99);
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
        request2.setUserId(1L);

        //when
        StorageOut storageOut = storageService.loan(request2);

        //then
        assertThat(storageOut.getRecordId()).isEqualTo(record.getId());
        assertNotNull(storageOut.getDueDate());
        assertNotNull(storageOut.getCreatedAt());

        // 한번 더 대출
        assertThatThrownBy(() -> storageService.loan(request2)).isInstanceOf(ApplicationException.class).hasMessage(RECORD_ALREADY_ON_LOAN);
    }

    @DisplayName("반납 성공")
    @Test
    void returns() {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus(REGISTER);
        request.setVisibility("공개");

        Records record = recordService.createRecord(request);

        LoanRequest request2 = new LoanRequest();
        request2.setRecordId(record.getId());
        request2.setUserId(1L);

        StorageOut storageOut = storageService.loan(request2);

        ReturnRequest request3 = new ReturnRequest();
        request3.setRecordId(record.getId());
        request3.setUserId(1L);

        //when
        StorageIn storageIn = storageService.returns(request3);
        Optional<StorageOut> findStorageOut = storageOutRepository.findByRecordId(record.getId());

        //then
        assertThat(storageIn.getRecordId()).isEqualTo(record.getId());
        assertNotNull(storageIn.getCreatedAt());
        assertTrue(findStorageOut.get().isDeleted());
    }

    @DisplayName("반납 실패 (대출중인 기록물이 아닌 경우)")
    @Test
    void return_fail1() {
        //given
        ReturnRequest request = new ReturnRequest();
        request.setRecordId(0L);

        //when
        //then
        assertThatThrownBy(() -> storageService.returns(request)).isInstanceOf(ApplicationException.class).hasMessage(RECORD_IS_NOT_ON_LOAN);
    }

    @DisplayName("반납연기 성공")
    @Test
    void delay() {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus(REGISTER);
        request.setVisibility("공개");

        Records record = recordService.createRecord(request);

        LoanRequest request2 = new LoanRequest();
        request2.setRecordId(record.getId());
        request2.setUserId(1L);

        StorageOut loan = storageService.loan(request2);

        ReturnDelayRequest request3 = new ReturnDelayRequest();
        request3.setRecordId(record.getId());

        //when
        StorageOut storageOut = storageService.delayReturn(request3);

        //then
        assertThat(storageOut.getDelayCount()).isEqualTo(loan.getDelayCount() + 1);
//        assertThat(storageOut.getDueDate()).isEqualTo(loan.getDueDate().plusDays(7));
        assertNotNull(storageOut.getUpdatedAt());
    }

    @DisplayName("반납연기 실패 (연기 횟수를 초과한 경우)")
    @Test
    void delay_fail1() {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus(REGISTER);
        request.setVisibility("공개");

        Records record = recordService.createRecord(request);

        LoanRequest request2 = new LoanRequest();
        request2.setRecordId(record.getId());
        request2.setUserId(1L);

        StorageOut loan = storageService.loan(request2);

        ReturnDelayRequest request3 = new ReturnDelayRequest();
        request3.setRecordId(record.getId());

        //when
        storageService.delayReturn(request3);
        storageService.delayReturn(request3);

        assertThatThrownBy(() -> storageService.delayReturn(request3)).isInstanceOf(ApplicationException.class).hasMessage(DELAY_LIMIT_EXCEEDED);
    }

    @DisplayName("반납연기 실패 (대출중인 기록물이 아닌 경우)")
    @Test
    void delay_fail2() {
        //given

        ReturnDelayRequest request3 = new ReturnDelayRequest();
        request3.setRecordId(0L);

        //when
        assertThatThrownBy(() -> storageService.delayReturn(request3)).isInstanceOf(ApplicationException.class).hasMessage(RECORD_IS_NOT_ON_LOAN);
    }

    @DisplayName("반납연기 성공")
    @Test
    void cancel() {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus(REGISTER);
        request.setVisibility("공개");

        Records record = recordService.createRecord(request);

        LoanRequest request2 = new LoanRequest();
        request2.setRecordId(record.getId());
        request2.setUserId(1L);

        StorageOut loan = storageService.loan(request2);

        CancelLoanRequest request3 = new CancelLoanRequest();
        request3.setStorageOutId(loan.getId());

        //when
        StorageOut storageOut = storageService.cancelLoan(request3);

        //then
        assertNotNull(storageOut.getDeletedAt());
    }

    @DisplayName("대출취소 실패 (대출중인 기록물이 아닌 경우)")
    @Test
    void cancel_fail() {
        //given

        CancelLoanRequest request3 = new CancelLoanRequest();
        request3.setStorageOutId(0L);

        //when
        assertThatThrownBy(() -> storageService.cancelLoan(request3)).isInstanceOf(ApplicationException.class).hasMessage(RECORD_IS_NOT_ON_LOAN);
    }
}