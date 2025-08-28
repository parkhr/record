package com.example.demo.economy.entity;

import com.example.demo.economy.domain.PayPolicy;
import com.example.demo.economy.request.CreateActiveRequest;
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
public class Active {

    @Id
    private long id;

    @Column("adminId")
    private long adminId;

    @Column("minutes")
    private int minutes;

    @Column("saved")
    private boolean saved;

    @Column("payPolicy")
    private PayPolicy payPolicy;

    @CreatedDate
    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public static Active createActive(CreateActiveRequest request, long adminId) {

        return Active.builder()
            .adminId(adminId)
            .minutes(request.getMinutes())
            .saved(false)
            .build();
    }

    public void save() {
        this.saved = true;
        this.payPolicy = PayPolicy.VERSION_2; // 적립 시점 정책으로 반영
        this.updatedAt = LocalDateTime.now();
    }

    public int getAmount() {
        int policyAmount = this.payPolicy.getPayPerMinute();

        return policyAmount * this.minutes;
    }

    public void cancelSave() {
        this.saved = false;
        this.updatedAt = LocalDateTime.now();
    }
}