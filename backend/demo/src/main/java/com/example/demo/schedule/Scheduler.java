package com.example.demo.schedule;

import com.example.demo.admin.entity.Admin;
import com.example.demo.admin.repository.AdminRepository;
import com.example.demo.economy.entity.Word;
import com.example.demo.economy.repository.WordRepository;
import com.example.demo.push.PushMessage;
import com.example.demo.push.PushSendResolver;
import com.example.demo.push.PushSender;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final PushSendResolver pushSendResolver;
    private final AdminRepository adminRepository;
    private final WordRepository wordRepository;

    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Seoul")
    public void recommendWord() {
        PushSender pushAppSender = pushSendResolver.resolve();
        Iterable<Admin> admins = adminRepository.findAll();

        List<Admin> activeAdmin = new ArrayList<>();
        for (Admin admin : admins) {
            if (!admin.isDeleted()) {
                activeAdmin.add(admin);
            }
        }

        PushMessage pushMessage = PushMessage.builder()
            .title("오늘의 추천 단어!")
            .body("")
            .build();

        for (Admin admin : activeAdmin) {
            if (!admin.isDeleted() && admin.isPushAgreed()) {
                //TODO 유저별 푸시 링크 다름

                List<Word> words = wordRepository.findByAdminIdAndCompletedLessThan(admin.getId(), 1);
                StringBuilder sb = new StringBuilder();

                for (Word word : words) {
                    sb.append(word.getName()).append(" : ").append(word.getMean()).append(" 노출 수 : ").append(word.getView()).append("\n");
                }

                pushMessage.setBody(sb.toString());

                pushAppSender.send(pushMessage);
            }
        }
    }

    @Scheduled(cron = "0 0 11 * * *", zone = "Asia/Seoul")
    public void checkUserActive() {
        PushSender pushAppSender = pushSendResolver.resolve();
        List<Admin> admins = adminRepository.findAdminsWithTodayNoActive();

        PushMessage pushMessage = PushMessage.builder()
            .title("오늘 활동, 아직 등록 안 하셨어요?")
            .body("기록은 작은 습관에서 시작돼요.  \n"
                + "오늘 어떤 활동을 하셨는지 떠오를 때 바로 등록해보세요!")
            .build();

        for (Admin admin : admins) {
            //TODO 유저별 푸시 링크 다름
            if (!admin.isDeleted() && admin.isPushAgreed()) {
                pushAppSender.send(pushMessage);
            }
        }
    }

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
            if (!admin.isDeleted() && admin.isPushAgreed()) {
                pushAppSender.send(pushMessage);
            }
        }
    }
}
