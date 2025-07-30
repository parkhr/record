package com.example.demo.layer.entity;

import com.example.demo.layer.request.CreateFolderRequest;
import com.example.demo.layer.request.UpdateFolderRequest;
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
public class Folder {

    @Id
    private long id;

    @Column("name")
    private String name;

    @Column("content")
    private String content;

    @Column("seriesId")
    private long seriesId;

    @Column("isUse")
    private boolean isUse;

    @CreatedDate
    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public static Folder createFolder(CreateFolderRequest request) {
        return Folder.builder()
            .name(request.getName())
            .content(request.getContent())
            .isUse(request.getIsUse())
            .seriesId(request.getSeriesId())
            .build();
    }

    public void update(UpdateFolderRequest request) {
        this.name = request.getName();
        this.content = request.getContent();
        this.isUse = request.getIsUse();
        this.seriesId = request.getSeriesId();
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void moveTempSeries() {
        this.seriesId = 0;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
