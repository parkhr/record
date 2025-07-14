package com.example.demo.entity;

import com.example.demo.request.CreateAdminRequest;
import com.example.demo.request.UpdateAdminRequest;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Admin {

    @Id
    private long id;

    @Column("name")
    private String name;

    @Column("password")
    private String password;

    @Column("roleId")
    private long roleId;

    @Column("isUse")
    private boolean isUse;

    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public static Admin createAdmin(CreateAdminRequest request) {
        return Admin.builder()
            .name(request.getName())
            .password(request.getPassword())
            .roleId(request.getRoleId())
            .isUse(true)
            .createdAt(LocalDateTime.now()).build();
    }

    public void update(UpdateAdminRequest request) {

        this.roleId = request.getRoleId();
        this.isUse = request.getIsUse();
        this.updatedAt = LocalDateTime.now();
    }

    public void resetPassword(String password) {
        this.password = password;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}

