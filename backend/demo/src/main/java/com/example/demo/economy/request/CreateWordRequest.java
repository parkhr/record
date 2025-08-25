package com.example.demo.economy.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWordRequest {

    private String name;
    private String mean;
    private String sentence;
}
