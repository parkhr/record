package com.example.demo.economy.response;

import com.example.demo.economy.request.TaskResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EpicResponse {

    private long id;
    private String title;
    private int sortOrder;
    private List<TaskResponse> todo;
}
