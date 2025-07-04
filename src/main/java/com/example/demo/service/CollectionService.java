package com.example.demo.service;

import static com.example.demo.common.ErrorMessage.COLLECTION_NOT_FOUND;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.entity.Collection;
import com.example.demo.entity.Series;
import com.example.demo.repository.CollectionRepository;
import com.example.demo.repository.SeriesRepository;
import com.example.demo.request.CreateCollectionRequest;
import com.example.demo.request.UpdateCollectionRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final SeriesRepository seriesRepository;

    @Transactional
    public Collection createCollection(CreateCollectionRequest request) {

        return collectionRepository.save(Collection.createCollection(request));
    }

    @Transactional
    public Collection updateCollection(UpdateCollectionRequest request) {

        Collection collection = collectionRepository.findById(request.getId()).orElseThrow(() -> new ApplicationException(COLLECTION_NOT_FOUND));

        collection.update(request);

        return collectionRepository.save(collection);
    }

    @Transactional
    public void deleteCollection(long collectionId) {
        Collection collection = collectionRepository.findById(collectionId).orElseThrow(() -> new ApplicationException(COLLECTION_NOT_FOUND));

        collection.delete();
        collectionRepository.save(collection);

        // 상위 컬렉션을 가지고 있는 시리즈 처리
        List<Series> findSeries = seriesRepository.findByCollectionId(collectionId);

        for (Series series : findSeries) {
            series.moveTempCollection();
            seriesRepository.save(series);
        }
    }

}
