package com.example.demo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRecordVisibilityRequest {

    private Long id;
    private String visibility;
}
