package com.example.demo.entity;

import com.example.demo.enums.PermissionType;
import com.example.demo.request.CreatePermissionRequest;
import com.example.demo.request.UpdatePermissionRequest;
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
public class Permission {

    @Id
    private long id;

    @Column("name")
    private PermissionType name;

    @Column("content")
    private String content;

    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public static Permission createTempPermission(CreatePermissionRequest request) {

        return Permission.builder()
            .name(request.getName())
            .content(request.getContent())
            .createdAt(LocalDateTime.now())
            .build();
    }

    public void update(UpdatePermissionRequest request) {
        this.content = request.getContent();
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
