package com.example.demo.economy.response;

import com.example.demo.economy.entity.Report;
import com.example.demo.economy.entity.ReportTask;
import com.example.demo.economy.request.TaskResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ReportResponse {

    private Report report;
    private List<ReportTask> reportTasks;
}
