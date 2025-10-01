package com.example.demo.economy;

import static com.example.demo.common.ErrorMessage.ADMIN_NOT_FOUND;

import com.example.demo.admin.entity.Admin;
import com.example.demo.admin.repository.AdminRepository;
import com.example.demo.common.CustomUserDetails;
import com.example.demo.common.exception.ApplicationException;
import com.example.demo.common.util.UserUtil;
import com.example.demo.economy.entity.Word;
import com.example.demo.economy.entity.WordCompleteLog;
import com.example.demo.economy.entity.WordLog;
import com.example.demo.economy.repository.WordCompleteLogRepository;
import com.example.demo.economy.repository.WordLogRepository;
import com.example.demo.economy.repository.WordRepository;
import com.example.demo.economy.request.CreateWordRequest;
import com.example.demo.economy.request.SearchWordRequest;
import com.example.demo.economy.request.UpdateWordRequest;
import com.example.demo.economy.response.SearchWordResponse;
import com.example.demo.economy.response.WordGameResponse;
import com.example.demo.economy.response.WordStatusResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordService {

    private final AdminRepository adminRepository;
    private final WordRepository wordRepository;
    private final WordLogRepository wordLogRepository;
    private final WordCompleteLogRepository wordCompleteLogRepository;

    @Transactional
    public Word createWord(CreateWordRequest request) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자입니다.");
        }

        Optional<Word> optionalWord = wordRepository.findByAdminIdAndName(admin.getId(), request.getName());
        if (optionalWord.isPresent()) {
            throw new ApplicationException("중복된 단어 입니다.");
        }

        return wordRepository.save(Word.createWord(request, admin.getId()));
    }

    @Transactional
    public void updateWord(UpdateWordRequest request) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자입니다.");
        }

        Word word = wordRepository.findById(request.getWordId()).orElseThrow(() -> new ApplicationException("단어가 없습니다."));

        if (word.isDeleted()) {
            throw new ApplicationException("삭제된 단어 입니다.");
        }

        if (admin.getId() != word.getAdminId()) {
            throw new ApplicationException("계정이 없습니다.");
        }

        // completed 가 4 -> 5 로 변경되는 시점에 로깅
        if (isWordMastered(word.getCompleted(), request.getCompleted())) {
            wordCompleteLogRepository.save(WordCompleteLog.createWordCompleteLog(admin.getId(), word.getId()));
        }

        word.update(request);
        wordRepository.save(word);
    }

    public Page<SearchWordResponse> findWords(SearchWordRequest request, Pageable pageable) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자입니다.");
        }

        return wordRepository.findWords(request, userDetails.getId(), pageable);
    }

    @Transactional
    public List<WordGameResponse> startWordGame() {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자입니다.");
        }

        wordLogRepository.save(WordLog.createWordLog(admin.getId()));

        // 외우지 못한 단어 전체 (최신 등록순)
        List<Word> unCompletedWord = wordRepository.findByAdminIdAndCompletedLessThan(admin.getId(), 5).stream()
            .sorted(Comparator.comparingLong(Word::getId).reversed()).toList();

        // 외운 단어 조회수 적은 순서대로 100개
//        List<Word> completedWord = wordRepository.findByAdminIdAndCompletedGreaterThanEqual(admin.getId(), 5).stream()
//            .sorted(Comparator.comparingInt(Word::getView)).limit(100).toList();

        //외운 단어 완전 랜덤 100개
        List<Word> completedWord = wordRepository.findRandomCompletedWords(admin.getId());

        List<Word> sessionWords = new ArrayList<>();

        sessionWords.addAll(unCompletedWord);
        sessionWords.addAll(completedWord);

        Collections.shuffle(sessionWords);

        return sessionWords.stream().map(item -> {
            WordGameResponse wordGameResponse = new WordGameResponse();
            wordGameResponse.setWordId(item.getId());
            wordGameResponse.setWord(item.getName());
            wordGameResponse.setMeaning(item.getMean());
            wordGameResponse.setExample(item.getSentence());
            wordGameResponse.setCompleted(item.getCompleted());
            wordGameResponse.setView(item.getView());
            return wordGameResponse;
        }).collect(Collectors.toList());
    }

    public int getTodayAttempts() {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자입니다.");
        }

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        List<WordLog> wordLogs = wordLogRepository.findByAdminIdAndCreatedAtBetween(admin.getId(), startOfDay, endOfDay);

        return wordLogs.size();
    }

    public WordStatusResponse getWordStatus() {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자입니다.");
        }

        // 외우지 못한 단어
        List<Word> unCompletedWord = wordRepository.findByAdminIdAndCompletedLessThan(admin.getId(), 5);

        // 외운 단어
        List<Word> completedWord = wordRepository.findByAdminIdAndCompletedGreaterThanEqual(admin.getId(), 5);

        WordStatusResponse response = new WordStatusResponse();
        response.setLearnedCount(completedWord.size());
        response.setUnLearnedCount(unCompletedWord.size());
        response.setTotalCount(completedWord.size() + unCompletedWord.size());
        return response;
    }

    private boolean isWordMastered(int beforeCompleteCount, int afterCompleteCount) {
        return beforeCompleteCount == 4 && afterCompleteCount == 5;
    }
}
