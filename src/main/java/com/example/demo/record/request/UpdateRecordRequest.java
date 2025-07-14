package com.example.demo.record.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRecordRequest {

    @NotBlank
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
