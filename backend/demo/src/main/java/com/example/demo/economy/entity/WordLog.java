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
public class WordLog {

    @Id
    private long id;

    @Column("adminId")
    private long adminId;

    @CreatedDate
    @Column("createdAt")
    private LocalDateTime createdAt;

    public static WordLog createWordLog(long adminId) {
        return WordLog.builder()
            .adminId(adminId)
            .build();
    }
}
