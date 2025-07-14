package com.example.demo.entity;

import static com.example.demo.common.ErrorMessage.DELAY_LIMIT_EXCEEDED;

import com.example.demo.common.exception.ApplicationException;
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

    @Column("userId")
    private long userId;

    @Column("dueDate")
    private LocalDateTime dueDate;

    @Column("delayCount")
    private int delayCount;

    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public static StorageOut createStorageOut(LoanRequest request) {
        return StorageOut.builder()
            .recordId(request.getRecordId())
            .userId(request.getUserId())
            .dueDate(LocalDateTime.now().plusDays(7))
            .delayCount(0)
            .createdAt(LocalDateTime.now())
            .build();
    }


    public void returns() {
        this.deletedAt = LocalDateTime.now();
    }

    public void delayReturn() {
        if (delayCount == 2) {
            throw new ApplicationException(DELAY_LIMIT_EXCEEDED);
        }

        this.delayCount = this.delayCount + 1;
        this.dueDate = this.dueDate.plusDays(7);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void cancel() {
        this.deletedAt = LocalDateTime.now();
    }
}
