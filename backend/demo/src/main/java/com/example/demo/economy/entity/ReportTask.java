package com.example.demo.economy.entity;

import com.example.demo.economy.request.CreateReportTaskRequest;
import com.example.demo.economy.request.UpdateReportTaskRequest;
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
public class ReportTask {

    @Id
    private long id;

    @Column("reportId")
    private long reportId;

    @Column("adminId")
    private long adminId;

    @Column("title")
    private String title;

    @Column("content")
    private String content;

    @Column("type")
    private String type;

    @Column("color")
    private String color;

    @Column("condition")
    private String condition;

    @Column("hard")
    private String hard;

    @Column("startTime")
    private LocalDateTime startTime;

    @Column("endTime")
    private LocalDateTime endTime;

    @CreatedDate
    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public static ReportTask createReportTask(CreateReportTaskRequest request, long adminId) {
        return ReportTask.builder()
            .adminId(adminId)
            .reportId(request.getReportId())
            .title(request.getTitle())
            .content(request.getContent())
            .type(request.getType())
            .color(request.getColor())
            .hard(request.getHard())
            .condition(request.getCondition())
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .build();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void update(UpdateReportTaskRequest request) {

        this.title = request.getTitle();
        this.content = request.getContent();
        this.type = request.getType();
        this.color = request.getColor();
        this.hard = request.getHard();
        this.condition = request.getCondition();
        this.startTime = request.getStartTime();
        this.endTime = request.getEndTime();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
//
//    public void delete() {
//        this.deletedAt = LocalDateTime.now();
//    }
//
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