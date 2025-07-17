package com.example.demo.layer;

import static com.example.demo.common.ErrorMessage.COLLECTION_NOT_FOUND;
import static com.example.demo.common.ErrorMessage.SERIES_NOT_FOUND;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.layer.entity.Folder;
import com.example.demo.layer.entity.Series;
import com.example.demo.layer.repository.CollectionRepository;
import com.example.demo.layer.repository.FolderRepository;
import com.example.demo.layer.repository.SeriesRepository;
import com.example.demo.layer.request.CreateSeriesRequest;
import com.example.demo.layer.request.UpdateSeriesRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final CollectionRepository collectionRepository;
    private final FolderRepository folderRepository;

    @Transactional
    public Series createSeries(CreateSeriesRequest request) {

        collectionRepository.findById(request.getCollectionId()).orElseThrow(() -> new ApplicationException(COLLECTION_NOT_FOUND));

        return seriesRepository.save(Series.createSeries(request));
    }

    @Transactional
    public Series updateSeries(UpdateSeriesRequest request) {

        collectionRepository.findById(request.getCollectionId()).orElseThrow(() -> new ApplicationException(COLLECTION_NOT_FOUND));
        Series series = seriesRepository.findById(request.getSeriesId()).orElseThrow(() -> new ApplicationException(SERIES_NOT_FOUND));

        series.update(request);

        return seriesRepository.save(series);
    }

    @Transactional
    public void deleteSeries(long seriesId) {
        Series series = seriesRepository.findById(seriesId).orElseThrow(() -> new ApplicationException(SERIES_NOT_FOUND));

        series.delete();
        seriesRepository.save(series);

        // 상위 시리즈를 가지고 있는 폴더 처리
        List<Folder> findFolder = folderRepository.findBySeriesId(seriesId);

        for (Folder folder : findFolder) {
            folder.moveTempSeries();
            folderRepository.save(folder);
        }
    }

}
