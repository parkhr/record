package com.example.demo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSeriesRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String content;

    @NotBlank
    private Long collectionId;

    @NotBlank
    private boolean isUse;
}
