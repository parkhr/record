package com.example.demo.service;

import static com.example.demo.common.ErrorMessage.FOLDER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.entity.Folder;
import com.example.demo.entity.Series;
import com.example.demo.repository.FolderRepository;
import com.example.demo.repository.SeriesRepository;
import com.example.demo.request.CreateFolderRequest;
import com.example.demo.request.UpdateFolderRequest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class FolderServiceTest {

    @Autowired
    private FolderService folderService;

    @Autowired
    private FolderRepository folderRepository;

    @MockitoBean
    private SeriesRepository seriesRepository;

    @DisplayName("폴더 생성 성공")
    @Test
    void createRecord() {
        //given
        CreateFolderRequest request = new CreateFolderRequest();
        request.setName("폴더제목");
        request.setContent("폴더내용");
        request.setSeriesId(0L);
        request.setUse(true);

        given(seriesRepository.findById(anyLong())).willReturn(Optional.ofNullable(Series.builder().build()));

        //when
        Folder folder = folderService.createFolder(request);

        //then
        assertThat(folder.getName()).isEqualTo("폴더제목");
        assertThat(folder.getContent()).isEqualTo("폴더내용");
        assertThat(folder.isUse()).isTrue();
        assertThat(folder.getSeriesId()).isEqualTo(0L);
        assertNotNull(folder.getCreatedAt());
    }

    @DisplayName("폴더 수정 성공")
    @Test
    void updateRecord() {
        //given

        CreateFolderRequest request = new CreateFolderRequest();
        request.setName("폴더제목");
        request.setContent("폴더내용");
        request.setSeriesId(0L);
        request.setUse(true);

        given(seriesRepository.findById(anyLong())).willReturn(Optional.ofNullable(Series.builder().build()));

        Folder folder = folderService.createFolder(request);

        UpdateFolderRequest updateRequest = new UpdateFolderRequest();
        updateRequest.setId(folder.getId());
        updateRequest.setName("수정된제목");
        updateRequest.setContent("수정된내용");
        updateRequest.setSeriesId(1L);
        updateRequest.setUse(false);

        //when
        Folder updateFolder = folderService.updateFolder(updateRequest);

        //then
        assertThat(updateFolder.getName()).isEqualTo("수정된제목");
        assertThat(updateFolder.getContent()).isEqualTo("수정된내용");
        assertThat(updateFolder.isUse()).isFalse();
        assertThat(updateFolder.getSeriesId()).isEqualTo(1L);
        assertNotNull(updateFolder.getUpdatedAt());
    }

    @DisplayName("폴더 수정 실패 (폴더이 존재하지 않는 경우)")
    @Test
    void updateRecord_fail1() {
        //given

        UpdateFolderRequest updateRequest = new UpdateFolderRequest();
        updateRequest.setId(0L);
        updateRequest.setName("수정된제목");
        updateRequest.setContent("수정된내용");
        updateRequest.setSeriesId(1L);
        updateRequest.setUse(false);

        given(seriesRepository.findById(anyLong())).willReturn(Optional.ofNullable(Series.builder().build()));

        //when
        //then
        assertThatThrownBy(() -> folderService.updateFolder(updateRequest)).isInstanceOf(ApplicationException.class).hasMessage(FOLDER_NOT_FOUND);
    }

    @DisplayName("폴더 삭제 성공")
    @Test
    void deleteRecord() {
        //given
        CreateFolderRequest request = new CreateFolderRequest();
        request.setName("폴더제목");
        request.setContent("폴더내용");
        request.setSeriesId(0L);
        request.setUse(true);

        given(seriesRepository.findById(anyLong())).willReturn(Optional.ofNullable(Series.builder().build()));

        Folder folder = folderService.createFolder(request);

        //when
        folderService.deleteFolder(folder.getId());

        // then
        Optional<Folder> deletedFolder = folderRepository.findById(folder.getId());
        assertTrue(deletedFolder.isPresent());
        assertNotNull(deletedFolder.get().getDeletedAt());
    }

    @DisplayName("폴더 삭제 실패 (폴더이 존재하지 않는 경우)")
    @Test
    void deleteRecord_fail1() {

        //when
        // then
        assertThatThrownBy(() -> folderService.deleteFolder(0L)).isInstanceOf(ApplicationException.class).hasMessage(FOLDER_NOT_FOUND);
    }
}