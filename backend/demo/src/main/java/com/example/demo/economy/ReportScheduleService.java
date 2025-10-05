package com.example.demo.economy;

import static com.example.demo.common.ErrorMessage.DELETED_ADMIN;

import com.example.demo.admin.entity.Admin;
import com.example.demo.admin.repository.AdminRepository;
import com.example.demo.common.exception.ApplicationException;
import com.example.demo.economy.entity.Report;
import com.example.demo.economy.entity.ReportConfig;
import com.example.demo.economy.entity.ReportTask;
import com.example.demo.economy.repository.ReportConfigRepository;
import com.example.demo.economy.repository.ReportRepository;
import com.example.demo.economy.repository.ReportTaskRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportScheduleService {

    private final AdminRepository adminRepository;
    private final ReportRepository reportRepository;
    private final ReportTaskRepository reportTaskRepository;
    private final ReportConfigRepository reportConfigRepository;

    @Transactional
    public void createReport(Admin admin) {

        if (admin.isDeleted()) {
            throw new ApplicationException(DELETED_ADMIN);
        }

        Report report = reportRepository.save(Report.createReport(admin.getId()));
        List<ReportConfig> reportConfigs = reportConfigRepository.findByAdminId(admin.getId()).stream().filter(item -> !item.isDeleted()).toList();

        for (ReportConfig config : reportConfigs) {
            reportTaskRepository.save(ReportTask.of(config, report.getId()));
        }
    }
}
