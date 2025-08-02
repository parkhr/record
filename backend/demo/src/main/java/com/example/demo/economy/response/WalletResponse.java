package com.example.demo.economy.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletResponse {
    private int amount;
    private int lastSaved;
    private int lastSpend;
}
