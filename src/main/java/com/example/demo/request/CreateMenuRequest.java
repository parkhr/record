package com.example.demo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMenuRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String content;
    @NotBlank
    private String link;
    @NotBlank
    private String link2;
}
