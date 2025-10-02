package com.example.demo.economy;

import com.example.demo.economy.request.CreateReportTaskRequest;
import com.example.demo.economy.request.UpdateReportTaskRequest;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/economy")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/report")
    public ResponseEntity<Object> findReport() {

        return ResponseEntity.ok().body(reportService.findReport());
    }

    @PostMapping("/report/task")
    public ResponseEntity<Object> createReportTask(@RequestBody CreateReportTaskRequest request) {

        return ResponseEntity.ok().body(reportService.createReportTask(request));
    }

    @PutMapping("/report/task")
    public ResponseEntity<Object> updateReportTask(@RequestBody UpdateReportTaskRequest request) {

        reportService.updateReportTask(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/report/task/{reportTaskId}")
    public ResponseEntity<Object> deleteReportTask(@PathVariable("reportTaskId") Long reportTaskId) {

        reportService.deleteReportTask(reportTaskId);
        return ResponseEntity.ok().build();
    }
}
