package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.entity.Records;
import com.example.demo.repository.RecordRepository;
import com.example.demo.request.CreateRecordRequest;
import com.example.demo.request.UpdateRecordRequest;
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

    @DisplayName("기록물을 생성한다.")
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
    }

    @DisplayName("기록물을 수정한다.")
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
    }

    @DisplayName("기록물을 삭제한다.")
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
}