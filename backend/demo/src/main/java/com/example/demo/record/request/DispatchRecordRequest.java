package com.example.demo.record.request;

import com.example.demo.enums.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DispatchRecordRequest {

    @NotNull(message = "기록물ID를 입력해주세요.")
    private Long recordId;

    @NotNull(message = "계층ID를 입력해주세요.")
    private Long locationId;

    @NotNull(message = "계층타입을 입력해주세요.")
    private Location locationType;
}
