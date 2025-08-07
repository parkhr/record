package com.example.demo.economy.domain;

public enum PayPolicy {
    VERSION_1(167);       // 2025년 최저시급 10030원에 기반한 분급 167원 - 20250808
    private final int payPerMinute;

    PayPolicy(int payPerMinute) {
        this.payPerMinute = payPerMinute;
    }

    public int getPayPerMinute() {
        return payPerMinute;
    }
}
