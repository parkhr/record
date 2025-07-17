package com.example.demo.record.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRecordRequest {

    @NotNull(message = "기록물ID를 입력해주세요.")
    private Long recordId;

    @NotBlank
    @Size(max = 100, message = "기록물명은 100자 이하로 입력해주세요.")
    private String title;

    @NotBlank
    @Size(max = 1000, message = "내용은 1000자 이하로 입력해주세요.")
    private String content;
}
