package com.example.demo.economy.domain;

public enum PayPolicy {
    VERSION_1(167),       // 2025년 최저시급 10030원에 기반한 분급 167원 - 20250808
    VERSION_2(310);       // 시급 149091원에 기반한 분급 310원 - 20250828
    private final int payPerMinute;

    PayPolicy(int payPerMinute) {
        this.payPerMinute = payPerMinute;
    }

    public int getPayPerMinute() {
        return payPerMinute;
    }
}