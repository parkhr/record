package com.example.demo.economy.entity;

import com.example.demo.economy.request.CreateWordRequest;
import com.example.demo.economy.request.UpdateWordRequest;
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
public class Word {

    @Id
    private long id;

    @Column("adminId")
    private long adminId;

    @Column("name")
    private String name;

    @Column("mean")
    private String mean;

    @Column("completed")
    private int completed;

    @Column("view")
    private int view;

    @Column("sentence")
    private String sentence;

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

    public static Word createWord(CreateWordRequest request, long adminId) {
        return Word.builder()
            .adminId(adminId)
            .name(request.getName())
            .mean(request.getMean())
            .completed(0)
            .view(0)
            .sentence(request.getSentence())
            .build();
    }

    public void update(UpdateWordRequest request) {
        this.mean = request.getMean();
        this.completed = request.getCompleted();
        this.view = request.getView();
        this.sentence = request.getSentence();
        this.updatedAt = LocalDateTime.now();
    }
}
