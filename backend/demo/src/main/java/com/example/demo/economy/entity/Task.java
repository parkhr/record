package com.example.demo.economy.entity;

import com.example.demo.economy.request.CreateTaskRequest;
import com.example.demo.economy.request.UpdateTaskRequest;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    private long id;

    @Column("adminId")
    private long adminId;

    @Column("title")
    private String title;

    @Column("content")
    private String content;

    @Column("startAt")
    private LocalDateTime startAt;

    @CreatedDate
    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public static Task createTask(CreateTaskRequest request, long adminId) {
        return Task.builder()
            .adminId(adminId)
            .title(request.getTitle())
            .content(request.getContent())
            .startAt(request.getStartAt())
            .build();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void update(UpdateTaskRequest request) {

    }
}