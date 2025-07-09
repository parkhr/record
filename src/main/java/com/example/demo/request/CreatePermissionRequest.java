package com.example.demo.request;

import com.example.demo.enums.PermissionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePermissionRequest {

    private PermissionType name;
    private String content;
}
