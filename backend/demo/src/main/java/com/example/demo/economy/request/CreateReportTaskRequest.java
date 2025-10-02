package com.example.demo.economy.request;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReportTaskRequest {
    
    private String title;
    private String content;
    private String type;
    private String color;
    private String condition;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
