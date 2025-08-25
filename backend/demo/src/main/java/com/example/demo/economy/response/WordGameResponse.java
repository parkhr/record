package com.example.demo.economy.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WordGameResponse {

    private long wordId;
    private String word;
    private String meaning;
    private String example;
    private int completed;
    private int view;
}
