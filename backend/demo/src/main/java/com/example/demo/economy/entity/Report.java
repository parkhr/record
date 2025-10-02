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
public class Report {

    @Id
    private long id;

    @Column("adminId")
    private long adminId;

    @Column("date")
    private LocalDateTime date;

    @CreatedDate
    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public static Report createReport(long adminId) {

        return Report.builder()
            .adminId(adminId)
            .date(LocalDateTime.now())
            .build();
    }

//    public void delete() {
//        this.deletedAt = LocalDateTime.now();
//    }
//
//    public boolean isDeleted() {
//        return this.deletedAt != null;
//    }
//
//    public void update(UpdateTaskRequest request) {
//        this.epicId = request.getEpicId();
//        this.title = request.getTitle();
//        this.content = request.getContent();
//        this.startAt = request.getStartAt();
//        this.completed = request.isCompleted();
//        this.sortOrder = request.getSortOrder();
//        this.updatedAt = LocalDateTime.now();
//    }
}