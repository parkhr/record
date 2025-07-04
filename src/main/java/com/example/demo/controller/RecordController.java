package com.example.demo.controller;

import com.example.demo.request.CreateRecordRequest;
import com.example.demo.request.DispatchRecordRequest;
import com.example.demo.request.SearchRecordRequest;
import com.example.demo.request.UpdateRecordRequest;
import com.example.demo.request.UpdateRecordStatusRequest;
import com.example.demo.request.UpdateRecordVisibilityRequest;
import com.example.demo.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/record")
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    public ResponseEntity<Object> createRecord(@RequestBody CreateRecordRequest request) {

        recordService.createRecord(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Object> updateRecord(@RequestBody UpdateRecordRequest request) {

        recordService.updateRecord(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<Object> deleteRecord(@PathVariable("recordId") Long recordId) {

        recordService.deleteRecord(recordId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findPublicRecords(SearchRecordRequest request, Pageable pageable) {
        return ResponseEntity.ok(recordService.findPublicRecords(request, pageable));
    }

    @PutMapping("/visibility")
    public ResponseEntity<Object> updateVisibility(UpdateRecordVisibilityRequest request) {

        recordService.updateVisibility(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/status")
    public ResponseEntity<Object> updateStatus(UpdateRecordStatusRequest request) {

        recordService.updateStatus(request);
        return ResponseEntity.ok().build();
    }

    // 기록물 배치
    @PostMapping("/dispatch")
    public ResponseEntity<Object> dispatchRecord(DispatchRecordRequest request) {

        recordService.dispatchRecord(request);
        return ResponseEntity.ok().build();
    }
}
