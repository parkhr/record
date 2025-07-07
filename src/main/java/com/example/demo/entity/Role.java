package com.example.demo.entity;

import com.example.demo.request.CreateRoleRequest;
import com.example.demo.request.UpdateRoleRequest;
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
public class Role {

    @Id
    private long id;

    @Column("name")
    private String name;

    @Column("content")
    private String content;

    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public static Role createTempRole(CreateRoleRequest request) {

        return Role.builder()
            .name(request.getName())
            .content(request.getContent())
            .createdAt(LocalDateTime.now())
            .build();
    }

    public void update(UpdateRoleRequest request) {
        this.content = request.getContent();
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
