package com.example.demo.reserve.entity;

import com.example.demo.reserve.request.CreateReserveRequest;
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
public class Reserve {

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

    public static Reserve createReserve(CreateReserveRequest request) {

        return Reserve.builder()
            .recordId(request.getRecordId())
            .userId(request.getUserId())
            .build();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void cancel() {
        this.deletedAt = LocalDateTime.now();
    }
}
