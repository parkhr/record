package com.example.demo.role.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoleRequest {

    @NotBlank
    @Size(max = 100, message = "권한그룹명은 100자 이하로 입력해주세요.")
    private String name;

    @NotBlank
    @Size(max = 1000, message = "내용은 100자 이하로 입력해주세요.")
    private String content;
}
