package com.example.demo.service;

import static com.example.demo.common.ErrorMessage.RECORD_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.entity.Records;
import com.example.demo.repository.RecordRepository;
import com.example.demo.request.CreateRecordRequest;
import com.example.demo.request.UpdateRecordRequest;
import com.example.demo.request.UpdateRecordStatusRequest;
import com.example.demo.request.UpdateRecordVisibilityRequest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class RecordServiceTest {

    @Autowired
    private RecordService recordService;

    @Autowired
    private RecordRepository recordRepository;

    @DisplayName("기록물 생성 성공")
    @Test
    void createRecord() {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus("임시");
        request.setVisibility("공개");

        //when
        Records record = recordService.createRecord(request);

        //then
        assertThat(record.getTitle()).isEqualTo("기록물제목");
        assertThat(record.getContent()).isEqualTo("기록물내용");
        assertThat(record.getStatus()).isEqualTo("임시");
        assertThat(record.getVisibility()).isEqualTo("공개");
        assertNotNull(record.getCreatedAt());
    }

    @DisplayName("기록물 수정 성공")
    @Test
    void updateRecord() {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus("임시");
        request.setVisibility("공개");

        Records record = recordService.createRecord(request);

        UpdateRecordRequest updateRequest = new UpdateRecordRequest();
        updateRequest.setId(record.getId());
        updateRequest.setTitle("수정된제목");
        updateRequest.setContent("수정된내용");

        //when
        Records updatedRecord = recordService.updateRecord(updateRequest);

        //then
        assertThat(updatedRecord.getTitle()).isEqualTo("수정된제목");
        assertThat(updatedRecord.getContent()).isEqualTo("수정된내용");
        assertNotNull(updatedRecord.getUpdatedAt());
    }

    @DisplayName("기록물 수정 실패 (기록물이 존재하지 않는 경우)")
    @Test
    void updateRecord_fail1() {
        //given

        UpdateRecordRequest updateRequest = new UpdateRecordRequest();
        updateRequest.setId(0L);
        updateRequest.setTitle("수정된제목");
        updateRequest.setContent("수정된내용");

        //when
        //then
        assertThatThrownBy(() -> recordService.updateRecord(updateRequest)).isInstanceOf(ApplicationException.class).hasMessage(RECORD_NOT_FOUND);
    }

    @DisplayName("기록물 삭제 성공")
    @Test
    void deleteRecord() {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus("임시");
        request.setVisibility("공개");

        Records record = recordService.createRecord(request);

        //when
        recordService.deleteRecord(record.getId());

        // then
        Optional<Records> deletedRecord = recordRepository.findById(record.getId());
        assertTrue(deletedRecord.isPresent());  // 1단계: 실제로는 DB엔 남아있고
        assertEquals("삭제", deletedRecord.get().getStatus());  // 2단계: 상태가 "삭제"인지 확인
        assertNotNull(deletedRecord.get().getDeletedAt());  // 3단계: 삭제 시간도 찍혀있는지
    }

    @DisplayName("기록물 삭제 실패 (기록물이 존재하지 않는 경우)")
    @Test
    void deleteRecord_fail1() {

        //when
        // then
        assertThatThrownBy(() -> recordService.deleteRecord(0L)).isInstanceOf(ApplicationException.class).hasMessage(RECORD_NOT_FOUND);
    }

    @DisplayName("기록물 공개유무 수정 성공")
    @Test
    void updateVisibility() {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus("임시");
        request.setVisibility("공개");

        Records record = recordService.createRecord(request);

        UpdateRecordVisibilityRequest request2 = new UpdateRecordVisibilityRequest();
        request2.setId(record.getId());
        request2.setVisibility("비공개");

        //when
        Records updatedRecord = recordService.updateVisibility(request2);

        // then
        assertThat(updatedRecord.getVisibility()).isEqualTo("비공개");
        assertNotNull(updatedRecord.getUpdatedAt());
    }

    @DisplayName("기록물 공개유무 수정 실패 (기록물이 없는 경우)")
    @Test
    void updateVisibility_fail1() {
        //given

        UpdateRecordVisibilityRequest request2 = new UpdateRecordVisibilityRequest();
        request2.setId(0L);
        request2.setVisibility("비공개");

        //when
        // then
        assertThatThrownBy(() -> recordService.updateVisibility(request2)).isInstanceOf(ApplicationException.class).hasMessage(RECORD_NOT_FOUND);
    }

    @DisplayName("기록물 상태 수정 성공")
    @Test
    void updateStatus() {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus("임시");
        request.setVisibility("공개");

        Records record = recordService.createRecord(request);

        UpdateRecordStatusRequest request2 = new UpdateRecordStatusRequest();
        request2.setId(record.getId());
        request2.setStatus("정식");

        //when
        Records updatedRecord = recordService.updateStatus(request2);

        // then
        assertThat(updatedRecord.getStatus()).isEqualTo("정식");
        assertNotNull(updatedRecord.getUpdatedAt());
    }

    @DisplayName("기록물 상태 수정 실패 (기록물이 없는 경우)")
    @Test
    void updateStatus_fail1() {
        //given

        UpdateRecordStatusRequest request2 = new UpdateRecordStatusRequest();
        request2.setId(0L);
        request2.setStatus("정식");

        //when
        // then
        assertThatThrownBy(() -> recordService.updateStatus(request2)).isInstanceOf(ApplicationException.class).hasMessage(RECORD_NOT_FOUND);
    }
}