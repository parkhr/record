package com.example.demo.service;

import static com.example.demo.common.ErrorMessage.SERIES_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.layer.entity.Collection;
import com.example.demo.layer.entity.Folder;
import com.example.demo.layer.FolderService;
import com.example.demo.layer.entity.Series;
import com.example.demo.layer.repository.CollectionRepository;
import com.example.demo.layer.repository.FolderRepository;
import com.example.demo.layer.repository.SeriesRepository;
import com.example.demo.layer.SeriesService;
import com.example.demo.layer.request.CreateFolderRequest;
import com.example.demo.layer.request.CreateSeriesRequest;
import com.example.demo.layer.request.UpdateSeriesRequest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class SeriesServiceTest {

    @Autowired
    private SeriesService seriesService;

    @Autowired
    private FolderService folderService;

    @Autowired
    private SeriesRepository seriesRepository;

    @MockitoBean
    private CollectionRepository collectionRepository;

    @Autowired
    private FolderRepository folderRepository;

    @DisplayName("시리즈 생성 성공")
    @Test
    void createRecord() {
        //given
        CreateSeriesRequest request = new CreateSeriesRequest();
        request.setName("시리즈제목");
        request.setContent("시리즈내용");
        request.setCollectionId(0L);
        request.setUse(true);

        given(collectionRepository.findById(anyLong())).willReturn(Optional.ofNullable(Collection.builder().build()));

        //when
        Series series = seriesService.createSeries(request);

        //then
        assertThat(series.getName()).isEqualTo("시리즈제목");
        assertThat(series.getContent()).isEqualTo("시리즈내용");
        assertThat(series.isUse()).isTrue();
        assertThat(series.getCollectionId()).isEqualTo(0L);
        assertNotNull(series.getCreatedAt());
    }

    @DisplayName("시리즈 수정 성공")
    @Test
    void updateRecord() {
        //given

        CreateSeriesRequest request = new CreateSeriesRequest();
        request.setName("시리즈제목");
        request.setContent("시리즈내용");
        request.setCollectionId(0L);
        request.setUse(true);

        given(collectionRepository.findById(anyLong())).willReturn(Optional.ofNullable(Collection.builder().build()));

        Series series = seriesService.createSeries(request);

        UpdateSeriesRequest updateRequest = new UpdateSeriesRequest();
        updateRequest.setId(series.getId());
        updateRequest.setName("수정된제목");
        updateRequest.setContent("수정된내용");
        updateRequest.setCollectionId(1L);
        updateRequest.setUse(false);

        //when
        Series updateSeries = seriesService.updateSeries(updateRequest);

        //then
        assertThat(updateSeries.getName()).isEqualTo("수정된제목");
        assertThat(updateSeries.getContent()).isEqualTo("수정된내용");
        assertThat(updateSeries.isUse()).isFalse();
        assertThat(updateSeries.getCollectionId()).isEqualTo(1L);
        assertNotNull(updateSeries.getUpdatedAt());
    }

    @DisplayName("시리즈 수정 실패 (시리즈이 존재하지 않는 경우)")
    @Test
    void updateRecord_fail1() {
        //given

        UpdateSeriesRequest updateRequest = new UpdateSeriesRequest();
        updateRequest.setId(0L);
        updateRequest.setName("수정된제목");
        updateRequest.setContent("수정된내용");
        updateRequest.setCollectionId(1L);
        updateRequest.setUse(false);

        given(collectionRepository.findById(anyLong())).willReturn(Optional.ofNullable(Collection.builder().build()));

        //when
        //then
        assertThatThrownBy(() -> seriesService.updateSeries(updateRequest)).isInstanceOf(ApplicationException.class).hasMessage(SERIES_NOT_FOUND);
    }

    @DisplayName("시리즈 삭제 성공")
    @Test
    void deleteRecord() {
        //given
        CreateSeriesRequest request = new CreateSeriesRequest();
        request.setName("시리즈제목");
        request.setContent("시리즈내용");
        request.setCollectionId(0L);
        request.setUse(true);

        given(collectionRepository.findById(anyLong())).willReturn(Optional.ofNullable(Collection.builder().build()));

        Series series = seriesService.createSeries(request);

        CreateFolderRequest request2 = new CreateFolderRequest();
        request2.setName("폴더제목");
        request2.setContent("폴더내용");
        request2.setSeriesId(series.getId());
        request2.setUse(true);

        Folder folder = folderService.createFolder(request2);

        //when
        seriesService.deleteSeries(series.getId());

        // then
        Optional<Series> deletedSeries = seriesRepository.findById(series.getId());
        assertTrue(deletedSeries.isPresent());
        assertNotNull(deletedSeries.get().getDeletedAt());

        //when
        Folder findFolder = folderRepository.findById(folder.getId()).get();

        //then
        assertThat(findFolder.getSeriesId()).isEqualTo(0L);
    }

    @DisplayName("시리즈 삭제 실패 (시리즈이 존재하지 않는 경우)")
    @Test
    void deleteRecord_fail1() {

        //when
        // then
        assertThatThrownBy(() -> seriesService.deleteSeries(0L)).isInstanceOf(ApplicationException.class).hasMessage(SERIES_NOT_FOUND);
    }
}