package com.example.demo.service;

import static com.example.demo.common.ErrorMessage.COLLECTION_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.entity.Collection;
import com.example.demo.repository.CollectionRepository;
import com.example.demo.request.CreateCollectionRequest;
import com.example.demo.request.UpdateCollectionRequest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CollectionServiceTest {

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private CollectionRepository collectionRepository;

    @DisplayName("컬렉션 생성 성공")
    @Test
    void createRecord() {
        //given
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName("컬렉션제목");
        request.setContent("컬렉션내용");
        request.setUse(true);

        //when
        Collection collection = collectionService.createCollection(request);

        //then
        assertThat(collection.getName()).isEqualTo("컬렉션제목");
        assertThat(collection.getContent()).isEqualTo("컬렉션내용");
        assertThat(collection.isUse()).isTrue();
    }

    @DisplayName("컬렉션 수정 성공")
    @Test
    void updateRecord() {
        //given
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName("컬렉션제목");
        request.setContent("컬렉션내용");
        request.setUse(true);

        Collection collection = collectionService.createCollection(request);

        UpdateCollectionRequest updateRequest = new UpdateCollectionRequest();
        updateRequest.setId(collection.getId());
        updateRequest.setName("수정된제목");
        updateRequest.setContent("수정된내용");
        updateRequest.setUse(false);

        //when
        Collection updateCollection = collectionService.updateCollection(updateRequest);

        //then
        assertThat(updateCollection.getName()).isEqualTo("수정된제목");
        assertThat(updateCollection.getContent()).isEqualTo("수정된내용");
        assertThat(updateCollection.isUse()).isFalse();
    }

    @DisplayName("컬렉션 수정 실패 (컬렉션이 존재하지 않는 경우)")
    @Test
    void updateRecord_fail1() {
        //given

        UpdateCollectionRequest updateRequest = new UpdateCollectionRequest();
        updateRequest.setId(0L);
        updateRequest.setName("수정된제목");
        updateRequest.setContent("수정된내용");
        updateRequest.setUse(false);

        //when
        //then
        assertThatThrownBy(() -> collectionService.updateCollection(updateRequest)).isInstanceOf(ApplicationException.class).hasMessage(COLLECTION_NOT_FOUND);
    }

    @DisplayName("컬렉션 삭제 성공")
    @Test
    void deleteRecord() {
        //given
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName("컬렉션제목");
        request.setContent("컬렉션내용");
        request.setUse(true);

        Collection collection = collectionService.createCollection(request);

        //when
        collectionService.deleteCollection(collection.getId());

        // then
        Optional<Collection> deletedCollection = collectionRepository.findById(collection.getId());
        assertTrue(deletedCollection.isPresent());
        assertNotNull(deletedCollection.get().getDeletedAt());
    }

    @DisplayName("컬렉션 삭제 실패 (컬렉션이 존재하지 않는 경우)")
    @Test
    void deleteRecord_fail1() {

        //when
        // then
        assertThatThrownBy(() -> collectionService.deleteCollection(0L)).isInstanceOf(ApplicationException.class).hasMessage(COLLECTION_NOT_FOUND);
    }
}