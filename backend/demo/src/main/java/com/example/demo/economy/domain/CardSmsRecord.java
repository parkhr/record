package com.example.demo.economy.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardSmsRecord {

    private String card;
    private String status;
    private String name;
    private int amount;
    private String paymentType;
    private String date;
    private String time;
    private String merchant;
    private int accumulated;

    public CardSmsRecord(String card, String status, String name, int amount, String paymentType,
        String date, String time, String merchant, int accumulated) {
        this.card = card;
        this.status = status;
        this.name = name;
        this.amount = amount;
        this.paymentType = paymentType;
        this.date = date;
        this.time = time;
        this.merchant = merchant;
        this.accumulated = accumulated;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s %d원 %s %s %s %s (누적 %d원)",
            card, status, name, amount, paymentType, date, time, merchant, accumulated);
    }
}
