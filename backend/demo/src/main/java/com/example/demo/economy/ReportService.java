package com.example.demo.economy;

import static com.example.demo.common.ErrorMessage.ADMIN_NOT_FOUND;
import static com.example.demo.common.ErrorMessage.DELETED_ADMIN;
import static com.example.demo.common.ErrorMessage.LOGIN_REQUIRED;

import com.example.demo.admin.entity.Admin;
import com.example.demo.admin.repository.AdminRepository;
import com.example.demo.common.CustomUserDetails;
import com.example.demo.common.exception.ApplicationException;
import com.example.demo.common.util.DateUtil;
import com.example.demo.common.util.UserUtil;
import com.example.demo.economy.entity.Report;
import com.example.demo.economy.entity.ReportTask;
import com.example.demo.economy.repository.ReportRepository;
import com.example.demo.economy.repository.ReportTaskRepository;
import com.example.demo.economy.request.CreateReportTaskRequest;
import com.example.demo.economy.request.UpdateReportTaskRequest;
import com.example.demo.economy.response.ReportResponse;
import com.example.demo.economy.response.ReportStatisticResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

        LocalDateTime[] rangeForDate = DateUtil.getUtcRangeForDate(LocalDate.now(ZoneId.of("Asia/Seoul")), ZoneId.of("Asia/Seoul"));

        Report report = reportRepository.findByAdminIdAndDateBetween(admin.getId(), rangeForDate[0], rangeForDate[1])
            .orElseThrow(() -> new ApplicationException("데일리리포트가 생성되지 않았습니다."));

        List<ReportTask> reportTasks = reportTaskRepository.findByAdminIdAndReportId(admin.getId(), report.getId()).stream()
            .filter(item -> !item.isDeleted()).toList();

        return ReportResponse.builder().report(report).reportTasks(reportTasks).build();
    }

    @Transactional
    public ReportTask createReportTask(CreateReportTaskRequest request) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException(LOGIN_REQUIRED));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException(DELETED_ADMIN);
        }

        Report report = reportRepository.findById(request.getReportId()).orElseThrow(() -> new ApplicationException("데일리리포트가 생성되지 않았습니다."));
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

    public ReportStatisticResponse statisticReportTask(long reportId) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException(LOGIN_REQUIRED));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException(DELETED_ADMIN);
        }

        List<ReportTask> reportTasks = reportTaskRepository.findByAdminIdAndReportId(admin.getId(), reportId);

        if (reportTasks.isEmpty()) {
            return new ReportStatisticResponse();
        } else {
            // 활동시간
            Map<String, List<ReportTask>> time = reportTasks.stream().collect(Collectors.groupingBy(ReportTask::getType));

            // 몰입도
            Map<String, List<ReportTask>> hard = reportTasks.stream().collect(Collectors.groupingBy(ReportTask::getHard));

            // 컨디션
            Map<String, List<ReportTask>> condition = reportTasks.stream().collect(Collectors.groupingBy(ReportTask::getCondition));

            ReportStatisticResponse response = new ReportStatisticResponse();
            response.setHard(hard);
            response.setCondition(condition);
            response.setTime(time);
            return response;
        }
    }
}
