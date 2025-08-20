package com.example.demo.economy.response;

import com.example.demo.economy.request.TaskResponse;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EpicResponse {

    private long id;
    private String title;
    private List<TaskResponse> todo;
}
