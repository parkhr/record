package com.example.demo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAdminRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String password;

    @NotBlank
    private String authGroup;
}
