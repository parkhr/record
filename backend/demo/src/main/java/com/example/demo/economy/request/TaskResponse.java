package com.example.demo.economy.request;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskResponse {

    private long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private int sortOrder;
}
