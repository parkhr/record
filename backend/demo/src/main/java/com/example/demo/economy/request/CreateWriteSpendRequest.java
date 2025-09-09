package com.example.demo.economy.request;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWriteSpendRequest {

    private int amount;
    private String place;
    private LocalDate date;
}
