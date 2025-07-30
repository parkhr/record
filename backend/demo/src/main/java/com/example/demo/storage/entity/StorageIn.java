package com.example.demo.storage.entity;

import com.example.demo.storage.request.ReturnRequest;
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
public class StorageIn {

    @Id
    private long id;

    @Column("recordId")
    private long recordId;

    @Column("userId")
    private long userId;

    @CreatedDate
    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;


    public static StorageIn createStorageIn(ReturnRequest request) {

        return StorageIn.builder()
            .recordId(request.getRecordId())
            .userId(request.getUserId())
            .build();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
