package com.example.demo.economy.entity;

import com.example.demo.economy.domain.CardSmsRecord;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
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
public class Spend {

    @Id
    private long id;

    @Column("amount")
    private int amount;

    @Column("place")
    private String place;

    @Column("deducted")
    private boolean deducted;

    @Column("spendAt")
    private LocalDateTime spendAt;

    @CreatedDate
    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public static Spend createSpend(CardSmsRecord cardSmsRecord) {
        // 날짜: MonthDay로 먼저 파싱
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd");
        MonthDay monthDay = MonthDay.parse(cardSmsRecord.getDate(), dateFormatter);

        // 원하는 연도 지정 (예: 현재 연도)
        int year = LocalDate.now().getYear();
        LocalDate date = monthDay.atYear(year);

        // 시간
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.parse(cardSmsRecord.getTime(), timeFormatter);

        return Spend.builder()
            .amount(cardSmsRecord.getAmount())
            .place(cardSmsRecord.getMerchant())
            .deducted(false)
            .spendAt(LocalDateTime.of(date, time)).build();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
