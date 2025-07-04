package com.example.demo.service;

import static com.example.demo.common.ErrorMessage.COLLECTION_NOT_FOUND;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.entity.Collection;
import com.example.demo.repository.CollectionRepository;
import com.example.demo.request.CreateCollectionRequest;
import com.example.demo.request.UpdateCollectionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;

    @Transactional
    public Collection createCollection(CreateCollectionRequest request) {

        return collectionRepository.save(Collection.createTempRecord(request));
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
    }

}
