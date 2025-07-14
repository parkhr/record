package com.example.demo.storage.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanRequest {

    @NotBlank
    public Long recordId;

    @NotBlank
    public Long userId;
}
