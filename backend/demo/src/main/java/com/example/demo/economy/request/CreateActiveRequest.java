package com.example.demo.economy.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateActiveRequest {

    private String type;
    private int minutes;
}
