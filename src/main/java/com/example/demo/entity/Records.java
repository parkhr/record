package com.example.demo.entity;

import static com.example.demo.enums.RecordStatus.DELETE;
import static com.example.demo.enums.RecordStatus.REGISTER;

import com.example.demo.enums.RecordStatus;
import com.example.demo.request.CreateRecordRequest;
import com.example.demo.request.UpdateRecordRequest;
import com.example.demo.request.UpdateRecordStatusRequest;
import com.example.demo.request.UpdateRecordVisibilityRequest;
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
public class Records {

    @Id
    private long id;

    @Column("title")
    private String title;

    @Column("content")
    private String content;

    @Column("status")
    private RecordStatus status;

    @Column("visibility")
    private String visibility;

    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public static Records createTempRecord(CreateRecordRequest request) {
        return Records.builder().title(request.getTitle()).content(request.getContent()).status(request.getStatus())
            .visibility(request.getVisibility()).createdAt(LocalDateTime.now()).build();
    }

    public void update(UpdateRecordRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.status = DELETE;
        this.deletedAt = LocalDateTime.now();
    }

    public void updateVisibility(UpdateRecordVisibilityRequest request) {
        this.visibility = request.getVisibility();
        this.updatedAt = LocalDateTime.now();
    }


    public void updateStatus(UpdateRecordStatusRequest request) {
        this.status = request.getStatus();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAvailableLoan() {
        return this.deletedAt == null && this.status == REGISTER && this.visibility.equals("공개");
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}