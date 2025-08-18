package com.example.demo.economy.entity;

import com.example.demo.economy.request.CreateEpicRequest;
import com.example.demo.economy.request.UpdateEpicRequest;
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
public class Epic {

    @Id
    private long id;

    @Column("adminId")
    private long adminId;

    @Column("title")
    private String title;

    @CreatedDate
    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public static Epic createEpic(CreateEpicRequest request, long adminId) {
        return Epic.builder().adminId(adminId).title(request.getTitle()).build();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void update(UpdateEpicRequest request) {
        this.title = request.getTitle();
    }
}