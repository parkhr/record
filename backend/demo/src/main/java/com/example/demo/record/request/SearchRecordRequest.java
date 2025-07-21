package com.example.demo.record.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRecordRequest {

    private String title;
    private String content;
    private String status;
    private String startDate;
    private String endDate;
}
