package com.example.demo.economy;

import static com.example.demo.common.ErrorMessage.ADMIN_NOT_FOUND;

import com.example.demo.admin.entity.Admin;
import com.example.demo.admin.repository.AdminRepository;
import com.example.demo.common.CustomUserDetails;
import com.example.demo.common.exception.ApplicationException;
import com.example.demo.common.util.UserUtil;
import com.example.demo.economy.entity.Word;
import com.example.demo.economy.repository.WordRepository;
import com.example.demo.economy.request.CreateWordRequest;
import com.example.demo.economy.request.SearchWordRequest;
import com.example.demo.economy.request.UpdateWordRequest;
import com.example.demo.economy.response.SearchWordResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

    public List<Word> startSession() {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자입니다.");
        }

        // 외우지 못한 단어 전체
        List<Word> unCompletedWord = wordRepository.findByAdminIdAndCompletedIsFalse(admin.getId());

        // 외운 단어 랜덤 100단어 까지
        List<Word> completedWord = wordRepository.findByAdminIdAndCompletedIsTrue(admin.getId());
        Collections.shuffle(completedWord);

        List<Word> random20 = completedWord.stream()
            .limit(100)
            .toList();

        List<Word> sessionWords = new ArrayList<>();
        sessionWords.addAll(unCompletedWord);
        sessionWords.addAll(random20);

        return sessionWords;
    }
}
