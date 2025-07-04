package com.example.demo.controller;

import com.example.demo.request.CreateFolderRequest;
import com.example.demo.request.CreateSeriesRequest;
import com.example.demo.request.UpdateFolderRequest;
import com.example.demo.request.UpdateSeriesRequest;
import com.example.demo.service.FolderService;
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
@RequestMapping("/api/folder")
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    public ResponseEntity<Object> createFolder(@RequestBody CreateFolderRequest request) {

        folderService.createFolder(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Object> updateFolder(@RequestBody UpdateFolderRequest request) {

        folderService.updateFolder(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<Object> deleteFolder(@PathVariable("folderId") Long folderId) {

        folderService.deleteFolder(folderId);
        return ResponseEntity.ok().build();
    }
}
