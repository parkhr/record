package com.example.demo.economy;

import static com.example.demo.common.ErrorMessage.ADMIN_NOT_FOUND;
import static com.example.demo.common.ErrorMessage.DELETED_ADMIN;
import static com.example.demo.common.ErrorMessage.LOGIN_REQUIRED;

import com.example.demo.admin.entity.Admin;
import com.example.demo.admin.repository.AdminRepository;
import com.example.demo.common.CustomUserDetails;
import com.example.demo.common.exception.ApplicationException;
import com.example.demo.common.util.UserUtil;
import com.example.demo.economy.entity.Report;
import com.example.demo.economy.entity.ReportTask;
import com.example.demo.economy.repository.ReportRepository;
import com.example.demo.economy.repository.ReportTaskRepository;
import com.example.demo.economy.request.CreateReportTaskRequest;
import com.example.demo.economy.request.UpdateReportTaskRequest;
import com.example.demo.economy.response.ReportResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final AdminRepository adminRepository;
    private final ReportRepository reportRepository;
    private final ReportTaskRepository reportTaskRepository;

    public ReportResponse findReport() {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException(LOGIN_REQUIRED));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException(DELETED_ADMIN);
        }

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay().minusNanos(1);

        Report report = reportRepository.findByAdminIdAndDateBetween(admin.getId(), start, end)
            .orElseThrow(() -> new ApplicationException("데일리리포트가 생성되지 않았습니다."));

        List<ReportTask> reportTasks = reportTaskRepository.findByAdminIdAndReportId(admin.getId(), report.getId());

        return ReportResponse.builder()
            .report(report)
            .reportTasks(reportTasks)
            .build();
    }

    @Transactional
    public ReportTask createReportTask(CreateReportTaskRequest request) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException(LOGIN_REQUIRED));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException(DELETED_ADMIN);
        }

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay().minusNanos(1);

        Report report = reportRepository.findByAdminIdAndDateBetween(admin.getId(), start, end)
            .orElseThrow(() -> new ApplicationException("데일리리포트가 생성되지 않았습니다."));

        return reportTaskRepository.save(ReportTask.createReportTask(request, admin.getId()));
    }

    @Transactional
    public void updateReportTask(UpdateReportTaskRequest request) {

        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException(LOGIN_REQUIRED));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException(DELETED_ADMIN);
        }

        ReportTask reportTask = reportTaskRepository.findById(request.getReportId())
            .orElseThrow(() -> new ApplicationException("데일리리포트 작업을 찾을 수 없습니다."));

        reportTask.update(request);
        reportTaskRepository.save(reportTask);
    }

    @Transactional
    public void deleteReportTask(long reportTaskId) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException(LOGIN_REQUIRED));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException(DELETED_ADMIN);
        }

        ReportTask reportTask = reportTaskRepository.findById(reportTaskId).orElseThrow(() -> new ApplicationException("데일리리포트 작업을 찾을 수 없습니다."));

        reportTask.delete();
        reportTaskRepository.save(reportTask);
    }
}
