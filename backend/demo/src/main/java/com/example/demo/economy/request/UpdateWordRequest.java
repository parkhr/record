package com.example.demo.economy.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateWordRequest {

    private Long wordId;
    private String mean;
    private boolean completed;
    private String sentence;
}
