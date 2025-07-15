package com.example.demo.layer.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCollectionRequest {

    @NotBlank
    @Size(max = 100, message = "컬렉션이름은 100자 이하로 입력해주세요.")
    private String name;

    @NotBlank
    @Size(max = 1000, message = "내용은 1000자 이하로 입력해주세요.")
    private String content;

    @NotBlank
    private boolean isUse;
}
