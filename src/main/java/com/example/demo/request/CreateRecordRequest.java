package com.example.demo.request;

import com.example.demo.enums.RecordStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRecordRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private RecordStatus status;

    @NotBlank
    private String visibility;
}
