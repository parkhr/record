package com.example.demo.economy.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchWordResponse {

    private long id;
    private int amount;
    private String name;
    private String mean;
    private boolean completed;
    private int view;
    private String sentence;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
