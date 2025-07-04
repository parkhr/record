package com.example.demo.request;

import com.example.demo.enums.Location;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DispatchRecordRequest {

    @NotBlank
    private Long recordId;

    @NotBlank
    private Long locationId;

    @NotBlank
    private Location locationType;
}
