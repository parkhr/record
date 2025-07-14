package com.example.demo.entity;

import com.example.demo.request.CreateCollectionRequest;
import com.example.demo.request.UpdateCollectionRequest;
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
public class Collection {

    @Id
    private long id;

    @Column("name")
    private String name;

    @Column("content")
    private String content;

    @Column("isUse")
    private boolean isUse;

    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public static Collection createCollection(CreateCollectionRequest request) {
        return Collection.builder()
            .name(request.getName())
            .content(request.getContent())
            .isUse(request.isUse())
            .createdAt(LocalDateTime.now())
            .build();
    }

    public void update(UpdateCollectionRequest request) {
        this.name = request.getName();
        this.content = request.getContent();
        this.isUse = request.isUse();
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
