package com.example.demo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRecordRequest {

    private String title;
    private String content;
    private String status;
}
