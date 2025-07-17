package com.example.demo.storage.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnRequest {

    @NotBlank
    public Long userId;

    @NotBlank
    public Long recordId;
}
