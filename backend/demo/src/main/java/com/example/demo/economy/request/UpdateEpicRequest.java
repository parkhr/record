package com.example.demo.economy.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEpicRequest {

    private long epicId;
    private String title;
    private int sortOrder;
}
