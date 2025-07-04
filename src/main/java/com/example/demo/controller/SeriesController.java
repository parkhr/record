package com.example.demo.controller;

import com.example.demo.request.CreateSeriesRequest;
import com.example.demo.request.UpdateSeriesRequest;
import com.example.demo.service.SeriesService;
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
@RequestMapping("/api/series")
public class SeriesController {

    private final SeriesService seriesService;

    @PostMapping
    public ResponseEntity<Object> createSeries(@RequestBody CreateSeriesRequest request) {

        seriesService.createSeries(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Object> updateSeries(@RequestBody UpdateSeriesRequest request) {

        seriesService.updateSeries(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{seriesId}")
    public ResponseEntity<Object> deleteSeries(@PathVariable("seriesId") Long seriesId) {

        seriesService.deleteSeries(seriesId);
        return ResponseEntity.ok().build();
    }
}
