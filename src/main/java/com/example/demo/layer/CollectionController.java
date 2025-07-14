package com.example.demo.layer;

import com.example.demo.layer.request.CreateCollectionRequest;
import com.example.demo.layer.request.UpdateCollectionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('WRITE_LAYER')")
    public ResponseEntity<Object> createCollection(@RequestBody CreateCollectionRequest request) {

        collectionService.createCollection(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @PreAuthorize("hasRole('UPDATE_LAYER')")
    public ResponseEntity<Object> updateCollection(@RequestBody UpdateCollectionRequest request) {

        collectionService.updateCollection(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{collectionId}")
    @PreAuthorize("hasRole('DELETE_LAYER')")
    public ResponseEntity<Object> deleteCollection(@PathVariable("collectionId") Long collectionId) {

        collectionService.deleteCollection(collectionId);
        return ResponseEntity.ok().build();
    }
}
