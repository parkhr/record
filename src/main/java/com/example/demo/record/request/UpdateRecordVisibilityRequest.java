package com.example.demo.record.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRecordVisibilityRequest {

    private Long id;
    private String visibility;
}
