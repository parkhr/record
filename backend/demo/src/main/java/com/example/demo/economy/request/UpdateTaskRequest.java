package com.example.demo.economy.request;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskRequest {

    private Long taskId;
    private Long epicId;
    private String title;
    private String content;
    private LocalDateTime startAt;
    private boolean completed;
    private Integer sortOrder;
}
