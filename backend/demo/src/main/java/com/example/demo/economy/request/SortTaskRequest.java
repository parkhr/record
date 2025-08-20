package com.example.demo.economy.request;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SortTaskRequest {

    private Long epicId;
    private List<Long> taskIds;
}
