package com.example.demo.schedule;

import com.example.demo.admin.entity.Admin;
import com.example.demo.admin.repository.AdminRepository;
import com.example.demo.economy.ReportScheduleService;
import com.example.demo.economy.entity.Report;
import com.example.demo.economy.repository.ReportRepository;
import com.example.demo.push.PushMessage;
import com.example.demo.push.PushSendResolver;
import com.example.demo.push.PushSender;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyReportScheduler {

    private final PushSendResolver pushSendResolver;
    private final AdminRepository adminRepository;
    private final ReportRepository reportRepository;
    private final ReportScheduleService reportScheduleService;

    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    public void createDailyReport() {
        Iterable<Admin> admins = adminRepository.findAll();

        for (Admin admin : admins) {
            if (!admin.isDeleted()) {
                reportScheduleService.createReport(admin);
            }
        }
    }

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void sendSuccessMessage() {
        Iterable<Admin> admins = adminRepository.findAll();

        PushSender pushAppSender = pushSendResolver.resolve();
        PushMessage pushMessage = PushMessage.builder()
            .title("데일리리포트가 생성되었어요.")
            .body("기록을 시작해보세요!")
            .build();

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay().minusNanos(1);

        for (Admin admin : admins) {
            if (!admin.isDeleted() && admin.isPushAgreed()) {
                Optional<Report> optionalReport = reportRepository.findByAdminIdAndDateBetween(admin.getId(), start, end);

                if (optionalReport.isPresent()) {
                    //TODO 유저별 푸시 링크 다름
                    pushAppSender.send(pushMessage);
                }
            }
        }
    }

}
