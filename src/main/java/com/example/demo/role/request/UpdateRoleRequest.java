package com.example.demo.role.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoleRequest {

    @NotNull(message = "권한ID를 입력해주세요.")
    private Long roleId;

    @NotBlank
    @Size(max = 1000, message = "내용은 1000자 이하로 입력해주세요.")
    private String content;

    private List<Long> permissionIds;
}
