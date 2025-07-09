package com.example.demo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateAdminRequest {

    @NotBlank
    private Long adminId;

    @NotBlank
    private Long roleId;

    @NotBlank
    private Boolean isUse;
}
