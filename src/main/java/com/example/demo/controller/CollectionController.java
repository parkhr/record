package com.example.demo.controller;

import com.example.demo.request.CreateCollectionRequest;
import com.example.demo.request.UpdateCollectionRequest;
import com.example.demo.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/collection")
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping
    public ResponseEntity<Object> createCollection(@RequestBody CreateCollectionRequest request) {

        collectionService.createCollection(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Object> updateCollection(@RequestBody UpdateCollectionRequest request) {

        collectionService.updateCollection(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{collectionId}")
    public ResponseEntity<Object> deleteCollection(@PathVariable("collectionId") Long collectionId) {

        collectionService.deleteCollection(collectionId);
        return ResponseEntity.ok().build();
    }
}
