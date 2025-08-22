package com.example.demo.economy;

import com.example.demo.economy.request.CreateWordRequest;
import com.example.demo.economy.request.UpdateWordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Object> createTask(@RequestBody CreateWordRequest request) {

        return ResponseEntity.ok().body(wordService.createWord(request));
    }

    @PutMapping("/word")
    public ResponseEntity<Object> updateTask(@RequestBody UpdateWordRequest request) {

        wordService.updateWord(request);
        return ResponseEntity.ok().build();
    }

}
