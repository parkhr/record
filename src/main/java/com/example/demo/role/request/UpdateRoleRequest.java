package com.example.demo.role.request;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoleRequest {

    private Long id;
    private String content;
    private List<Long> permissionIds;
}
