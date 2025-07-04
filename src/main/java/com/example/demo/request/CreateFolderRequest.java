package com.example.demo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFolderRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String content;

    @NotBlank
    private Long seriesId;

    @NotBlank
    private boolean isUse;
}
