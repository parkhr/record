package com.example.demo.economy.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchWordRequest {

    private String name;
    private String startDate;
    private String endDate;
    private String sortBy;
    private String order;
}
