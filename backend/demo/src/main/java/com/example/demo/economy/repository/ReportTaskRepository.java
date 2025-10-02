package com.example.demo.economy.repository;

import com.example.demo.economy.entity.ReportTask;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ReportTaskRepository extends CrudRepository<ReportTask, Long> {

    List<ReportTask> findByAdminIdAndReportId(long adminId, long reportId);
}
