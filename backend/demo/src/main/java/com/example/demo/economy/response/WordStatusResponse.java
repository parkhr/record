package com.example.demo.economy.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WordStatusResponse {

    private int learnedCount;
    private int unLearnedCount;
    private int totalCount;
}
