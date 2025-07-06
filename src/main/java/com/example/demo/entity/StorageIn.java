package com.example.demo.entity;

import com.example.demo.request.ReturnRequest;
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
public class StorageIn {

    @Id
    private long id;

    @Column("recordId")
    private long recordId;

    @Column("userId")
    private long userId;

    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;


    public static StorageIn createStorageIn(ReturnRequest request) {

        return StorageIn.builder()
            .recordId(request.getRecordId())
            .createdAt(LocalDateTime.now())
            .userId(request.getUserId())
            .build();
    }
}
