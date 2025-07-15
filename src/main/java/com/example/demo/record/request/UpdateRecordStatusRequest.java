package com.example.demo.record.request;

import com.example.demo.enums.RecordStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRecordStatusRequest {

    @NotNull(message = "기록물ID를 입력해주세요.")
    private Long recordId;

    @NotNull(message = "기록물 상태를 입력해주세요.")
    private RecordStatus status;
}
