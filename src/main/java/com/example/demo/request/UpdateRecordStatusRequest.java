package com.example.demo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRecordStatusRequest {

    private Long id;
    private String status;
}
