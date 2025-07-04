package com.example.demo.request;

import com.example.demo.enums.RecordStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRecordStatusRequest {

    private Long id;
    private RecordStatus status;
}
