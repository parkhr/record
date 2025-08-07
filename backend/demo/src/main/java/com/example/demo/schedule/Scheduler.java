package com.example.demo.schedule;

import com.example.demo.admin.entity.Admin;
import com.example.demo.admin.repository.AdminRepository;
import com.example.demo.push.PushMessage;
import com.example.demo.push.PushSendResolver;
import com.example.demo.push.PushSender;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final PushSendResolver pushSendResolver;
    private final AdminRepository adminRepository;

    @Scheduled(cron = "0 0 21 * * *", zone = "Asia/Seoul")
    public void checkUserSpending() {
        PushSender pushAppSender = pushSendResolver.resolve();
        List<Admin> admins = adminRepository.findAdminsWithTodayNoSpend();

        PushMessage pushMessage = PushMessage.builder()
            .title("오늘 지출, 아직 안 하셨어요?")
            .body("하루의 마무리는 지출 등록부터!  \n"
                + "오늘 소비한 내역을 잊지 말고 등록해주세요.  \n"
                + "지금 바로 입력하고, 습관을 만들어볼까요?")
            .build();

        for (Admin admin : admins) {
            //TODO 유저별 푸시 링크 다름
            pushAppSender.send(pushMessage);
        }
    }
}
