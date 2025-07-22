package com.example.demo.menu.entity;

import com.example.demo.menu.request.CreateMenuRequest;
import com.example.demo.menu.request.UpdateMenuRequest;
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
public class Menu {

    @Id
    private long id;

    @Column("parentId")
    private long parentId;

    @Column("name")
    private String name;

    @Column("content")
    private String content;

    @Column("link")
    private String link;

    @Column("openType")
    private String openType;

    @Column("menuLevel")
    private int menuLevel;

    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public static Menu createMenu(CreateMenuRequest request) {
        return Menu.builder()
            .name(request.getName())
            .content(request.getContent())
            .link(request.getLink())
            .openType(request.getOpenType())
            .menuLevel(request.getMenuLevel())
            .createdAt(LocalDateTime.now())
            .build();
    }

    public void update(UpdateMenuRequest request) {
        this.name = request.getName();
        this.content = request.getContent();
        this.link = request.getLink();
        this.openType = request.getOpenType();
        this.menuLevel = request.getMenuLevel();
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
