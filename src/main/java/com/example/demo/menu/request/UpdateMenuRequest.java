package com.example.demo.menu.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMenuRequest {

    @NotBlank
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String content;
    @NotBlank
    private String link;
    @NotBlank
    private String link2;
}
