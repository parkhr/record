package com.example.demo.economy;

import com.example.demo.economy.request.CreateWordRequest;
import com.example.demo.economy.request.SearchWordRequest;
import com.example.demo.economy.request.UpdateWordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/economy")
public class WordController {

    private final WordService wordService;

    @PostMapping("/word")
    public ResponseEntity<Object> createWord(@RequestBody CreateWordRequest request) {

        return ResponseEntity.ok().body(wordService.createWord(request));
    }

    @PutMapping("/word")
    public ResponseEntity<Object> updateWord(@RequestBody UpdateWordRequest request) {

        wordService.updateWord(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/word/search")
    public ResponseEntity<Object> findWords(SearchWordRequest request, Pageable pageable) {
        return ResponseEntity.ok(wordService.findWords(request, pageable));
    }

    @GetMapping("/word/game")
    public ResponseEntity<Object> startWordGame() {
        return ResponseEntity.ok(wordService.startWordGame());
    }

    @GetMapping("/word/attempts")
    public ResponseEntity<Object> getAttempts() {

        return ResponseEntity.ok(wordService.getTodayAttempts());
    }

}
