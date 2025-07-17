package com.example.demo.service;

import static com.example.demo.common.ErrorMessage.COLLECTION_NOT_FOUND;
import static com.example.demo.common.ErrorMessage.RECORD_NOT_FOUND;
import static com.example.demo.enums.Location.COLLECTION;
import static com.example.demo.enums.RecordStatus.DELETE;
import static com.example.demo.enums.RecordStatus.REGISTER;
import static com.example.demo.enums.RecordStatus.TEMP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.layer.entity.Collection;
import com.example.demo.layer.CollectionService;
import com.example.demo.record.entity.RecordLocation;
import com.example.demo.record.RecordService;
import com.example.demo.record.entity.Records;
import com.example.demo.record.repository.RecordRepository;
import com.example.demo.layer.request.CreateCollectionRequest;
import com.example.demo.record.request.CreateRecordRequest;
import com.example.demo.record.request.DispatchRecordRequest;
import com.example.demo.record.request.UpdateRecordRequest;
import com.example.demo.record.request.UpdateRecordStatusRequest;
import com.example.demo.record.request.UpdateRecordVisibilityRequest;
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
    private CollectionService collectionService;

    @Autowired
    private RecordRepository recordRepository;

    @DisplayName("기록물 생성 성공")
    @Test
    void createRecord() {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus(TEMP);
        request.setIsPublic(true);

        //when
        Records record = recordService.createRecord(request);

        //then
        assertThat(record.getTitle()).isEqualTo("기록물제목");
        assertThat(record.getContent()).isEqualTo("기록물내용");
        assertThat(record.getStatus()).isEqualTo(TEMP);
        assertTrue(record.isPublic());
        assertNotNull(record.getCreatedAt());
    }

    @DisplayName("기록물 수정 성공")
    @Test
    void updateRecord() {
        //given
        CreateRecordRequest request = new CreateRecordRequest();
        request.setTitle("기록물제목");
        request.setContent("기록물내용");
        request.setStatus(TEMP);
        request.setIsPublic(true);

        Records record = recordService.createRecord(request);

        UpdateRecordRequest updateRequest = new UpdateRecordRequest();
        updateRequest.setRecordId(record.getId());
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
        updateRequest.setRecordId(0L);
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
        request.setStatus(TEMP);
        request.setIsPublic(true);

        Records record = recordService.createRecord(request);

        //when
        recordService.deleteRecord(record.getId());

        // then
        Optional<Records> deletedRecord = recordRepository.findById(record.getId());
        assertTrue(deletedRecord.isPresent());  // 1단계: 실제로는 DB엔 남아있고
        assertEquals(DELETE, deletedRecord.get().getStatus());  // 2단계: 상태가 "삭제"인지 확인
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
        request.setStatus(TEMP);
        request.setIsPublic(true);

        Records record = recordService.createRecord(request);

        UpdateRecordVisibilityRequest request2 = new UpdateRecordVisibilityRequest();
        request2.setRecordId(record.getId());
        request2.setIsPublic(false);

        //when
        Records updatedRecord = recordService.updateVisibility(request2);

        // then
        assertFalse(updatedRecord.isPublic());
        assertNotNull(updatedRecord.getUpdatedAt());
    }

    @DisplayName("기록물 공개유무 수정 실패 (기록물이 없는 경우)")
    @Test
    void updateVisibility_fail1() {
        //given

        UpdateRecordVisibilityRequest request2 = new UpdateRecordVisibilityRequest();
        request2.setRecordId(0L);
        request2.setIsPublic(false);

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
        request.setStatus(TEMP);
        request.setIsPublic(true);

        Records record = recordService.createRecord(request);

        UpdateRecordStatusRequest request2 = new UpdateRecordStatusRequest();
        request2.setRecordId(record.getId());
        request2.setStatus(REGISTER);

        //when
        Records updatedRecord = recordService.updateStatus(request2);

        // then
        assertThat(updatedRecord.getStatus()).isEqualTo(REGISTER);
        assertNotNull(updatedRecord.getUpdatedAt());
    }

    @DisplayName("기록물 상태 수정 실패 (기록물이 없는 경우)")
    @Test
    void updateStatus_fail1() {
        //given

        UpdateRecordStatusRequest request2 = new UpdateRecordStatusRequest();
        request2.setRecordId(0L);
        request2.setStatus(REGISTER);

        //when
        // then
        assertThatThrownBy(() -> recordService.updateStatus(request2)).isInstanceOf(ApplicationException.class).hasMessage(RECORD_NOT_FOUND);
    }

    @DisplayName("기록물 배치 성공")
    @Test
    void dispatch() {
        //given
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName("컬렉션제목");
        request.setContent("컬렉션내용");
        request.setUse(true);

        Collection collection = collectionService.createCollection(request);

        CreateRecordRequest request2 = new CreateRecordRequest();
        request2.setTitle("기록물제목");
        request2.setContent("기록물내용");
        request2.setStatus(TEMP);
        request2.setIsPublic(true);

        Records record = recordService.createRecord(request2);

        DispatchRecordRequest request3 = new DispatchRecordRequest();

        request3.setRecordId(record.getId());
        request3.setLocationId(collection.getId());
        request3.setLocationType(COLLECTION);

        //when
        RecordLocation recordLocation = recordService.dispatchRecord(request3);

        // then
        assertThat(recordLocation.getRecordId()).isEqualTo(record.getId());
        assertThat(recordLocation.getLocationId()).isEqualTo(collection.getId());
        assertThat(recordLocation.getLocationType()).isEqualTo(COLLECTION);
    }

    @DisplayName("기록물 배치 실패 (기록물이 없는 경우)")
    @Test
    void dispatch_fail1() {
        DispatchRecordRequest request3 = new DispatchRecordRequest();

        request3.setRecordId(0L);
        request3.setLocationId(0L);
        request3.setLocationType(COLLECTION);

        //when
        // then
        assertThatThrownBy(() -> recordService.dispatchRecord(request3)).isInstanceOf(ApplicationException.class).hasMessage(RECORD_NOT_FOUND);
    }

    @DisplayName("기록물 배치 실패 (계층구조가 없는 경우)")
    @Test
    void dispatch_fail2() {

        CreateRecordRequest request2 = new CreateRecordRequest();
        request2.setTitle("기록물제목");
        request2.setContent("기록물내용");
        request2.setStatus(TEMP);
        request2.setIsPublic(true);

        Records record = recordService.createRecord(request2);

        DispatchRecordRequest request3 = new DispatchRecordRequest();

        request3.setRecordId(record.getId());
        request3.setLocationId(0L);
        request3.setLocationType(COLLECTION);

        //when
        // then
        assertThatThrownBy(() -> recordService.dispatchRecord(request3)).isInstanceOf(ApplicationException.class).hasMessage(COLLECTION_NOT_FOUND);
    }
}