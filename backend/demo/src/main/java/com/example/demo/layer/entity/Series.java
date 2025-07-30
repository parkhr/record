package com.example.demo.layer.entity;

import com.example.demo.layer.request.CreateSeriesRequest;
import com.example.demo.layer.request.UpdateSeriesRequest;
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
public class Series {

    @Id
    private long id;

    @Column("name")
    private String name;

    @Column("content")
    private String content;

    @Column("collectionId")
    private long collectionId;

    @Column("isUse")
    private boolean isUse;

    @CreatedDate
    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public static Series createSeries(CreateSeriesRequest request) {
        return Series.builder()
            .name(request.getName())
            .content(request.getContent())
            .isUse(request.getIsUse())
            .collectionId(request.getCollectionId())
            .build();
    }

    public void update(UpdateSeriesRequest request) {
        this.name = request.getName();
        this.content = request.getContent();
        this.isUse = request.getIsUse();
        this.collectionId = request.getCollectionId();
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void moveTempCollection() {
        this.collectionId = 0;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
