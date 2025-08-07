package com.example.demo.economy.entity;

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
public class WalletLog {

    @Id
    private long id;

    @Column("status")
    private String status;

    @Column("adminId")
    private long adminId;

    @Column("amount")
    private int amount;

    @CreatedDate
    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public static WalletLog createWalletLog(String status, long adminId, int amount) {
        return WalletLog.builder()
            .status(status)
            .adminId(adminId)
            .amount(amount)
            .build();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}