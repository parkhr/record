package com.example.demo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateFolderRequest {

    @NotBlank
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String content;

    @NotBlank
    private boolean isUse;

    @NotBlank
    private Long seriesId;
}
