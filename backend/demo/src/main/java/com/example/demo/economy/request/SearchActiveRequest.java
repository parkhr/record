package com.example.demo.economy.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchActiveRequest {

    private String status;
    private String startDate;
    private String endDate;
}
