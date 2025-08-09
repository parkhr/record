package com.example.demo.admin.entity;

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
public class LoginLog {

    @Id
    private long id;

    @Column("adminId")
    private long adminId;

    @CreatedDate
    @Column("createdAt")
    private LocalDateTime createdAt;

    public static LoginLog createLoginLog(long adminId) {
        return LoginLog.builder()
            .adminId(adminId)
            .build();
    }
}
