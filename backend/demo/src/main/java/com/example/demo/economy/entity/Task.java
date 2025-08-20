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

    @Column("epicId")
    private long epicId;

    @Column("adminId")
    private long adminId;

    @Column("title")
    private String title;

    @Column("content")
    private String content;

    @Column("startAt")
    private LocalDateTime startAt;

    @Column("completed")
    private boolean completed;

    @Column("sortOrder")
    private int sortOrder;

    @CreatedDate
    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public static Task createTask(CreateTaskRequest request, long adminId) {
        return Task.builder()
            .epicId(request.getEpicId())
            .adminId(adminId)
            .title(request.getTitle())
            .content(request.getContent())
            .startAt(request.getStartAt())
            .completed(false)
            .sortOrder(0)
            .build();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void update(UpdateTaskRequest request) {
        this.epicId = request.getEpicId();
        this.title = request.getTitle();
        this.content = request.getContent();
        this.startAt = request.getStartAt();
        this.completed = request.isCompleted();
        this.sortOrder = request.getSortOrder();
        this.updatedAt = LocalDateTime.now();
    }
}