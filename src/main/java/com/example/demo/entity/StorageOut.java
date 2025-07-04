package com.example.demo.entity;

import com.example.demo.request.LoanRequest;
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
public class StorageOut {

    @Id
    private long id;

    @Column("recordId")
    private long recordId;

    @Column("dueDate")
    private LocalDateTime dueDate;

    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public static StorageOut createStorageOut(LoanRequest request) {
        return StorageOut.builder()
            .recordId(request.getRecordId())
            .dueDate(LocalDateTime.now().plusDays(7))
            .createdAt(LocalDateTime.now())
            .build();
    }


    public void returns() {
        this.deletedAt = LocalDateTime.now();
    }
}
