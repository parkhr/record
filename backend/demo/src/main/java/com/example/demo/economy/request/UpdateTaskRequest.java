package com.example.demo.economy.request;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskRequest {

    private long taskId;
    private String title;
    private String content;
    private LocalDateTime startAt;
}
