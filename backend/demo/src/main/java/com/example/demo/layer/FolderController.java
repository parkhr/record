package com.example.demo.layer;

import com.example.demo.layer.request.CreateFolderRequest;
import com.example.demo.layer.request.UpdateFolderRequest;
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
@RequestMapping("/api/folder")
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    @PreAuthorize("hasRole('WRITE_LAYER')")
    public ResponseEntity<Object> createFolder(@RequestBody CreateFolderRequest request) {

        folderService.createFolder(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @PreAuthorize("hasRole('UPDATE_LAYER')")
    public ResponseEntity<Object> updateFolder(@RequestBody UpdateFolderRequest request) {

        folderService.updateFolder(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{folderId}")
    @PreAuthorize("hasRole('DELETE_LAYER')")
    public ResponseEntity<Object> deleteFolder(@PathVariable("folderId") Long folderId) {

        folderService.deleteFolder(folderId);
        return ResponseEntity.ok().build();
    }
}
