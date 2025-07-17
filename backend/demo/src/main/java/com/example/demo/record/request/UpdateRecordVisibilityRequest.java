package com.example.demo.record.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRecordVisibilityRequest {

    @NotNull(message = "기록물ID를 입력해주세요.")
    private Long recordId;

    @NotNull
    private Boolean isPublic;
}
