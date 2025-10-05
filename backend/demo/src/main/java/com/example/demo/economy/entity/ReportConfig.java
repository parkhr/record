package com.example.demo.economy.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
public class ReportConfig {

    @Id
    private long id;

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
    private LocalTime startTime;

    @Column("endTime")
    private LocalTime endTime;

    @CreatedDate
    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
