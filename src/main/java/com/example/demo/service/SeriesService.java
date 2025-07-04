package com.example.demo.service;

import static com.example.demo.common.ErrorMessage.SERIES_NOT_FOUND;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.entity.Series;
import com.example.demo.repository.SeriesRepository;
import com.example.demo.request.CreateSeriesRequest;
import com.example.demo.request.UpdateSeriesRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository seriesRepository;

    @Transactional
    public Series createSeries(CreateSeriesRequest request) {

        return seriesRepository.save(Series.createSeries(request));
    }

    @Transactional
    public Series updateSeries(UpdateSeriesRequest request) {

        Series series = seriesRepository.findById(request.getId()).orElseThrow(() -> new ApplicationException(SERIES_NOT_FOUND));

        series.update(request);

        return seriesRepository.save(series);
    }

    @Transactional
    public void deleteSeries(long collectionId) {
        Series series = seriesRepository.findById(collectionId).orElseThrow(() -> new ApplicationException(SERIES_NOT_FOUND));

        series.delete();
        seriesRepository.save(series);
    }

}
