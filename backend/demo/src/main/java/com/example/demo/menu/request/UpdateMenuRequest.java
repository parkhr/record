package com.example.demo.menu.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMenuRequest {

    @NotBlank
    private Long menuId;
    @NotBlank
    private String name;
    @NotBlank
    private String content;
    @NotBlank
    private String link;
    @NotBlank
    private String openType;
    @NotBlank
    private Integer menuLevel;
}
