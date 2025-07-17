package com.example.demo.layer.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFolderRequest {

    @NotBlank
    @Size(max = 100, message = "폴더명은 100자 이하로 입력해주세요.")
    private String name;

    @NotBlank
    @Size(max = 1000, message = "내용은 1000자 이하로 입력해주세요.")
    private String content;

    @NotNull(message = "시리즈ID를 입력해주세요.")
    private Long seriesId;

    @NotNull(message = "사용유무를 입력해주세요.")
    private Boolean isUse;
}
