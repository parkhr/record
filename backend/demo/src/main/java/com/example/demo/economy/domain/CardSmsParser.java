package com.example.demo.economy.domain;

import com.example.demo.common.exception.ApplicationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardSmsParser {

    public static CardSmsRecord parse(String sms) {
        Pattern pattern = Pattern.compile(
            "(.+?)승인\\s+(.+?)\\s+([\\d,]+)원\\s+(일시불|\\d+개월)\\s+(\\d{2}/\\d{2})\\s+(\\d{2}:\\d{2})\\s+(.+?)\\s+누적([\\d,]+)원"
        );

        Matcher matcher = pattern.matcher(sms);

        if (matcher.find()) {
            String card = matcher.group(1).trim();
            String name = matcher.group(2).trim();
            int amount = Integer.parseInt(matcher.group(3).replace(",", ""));
            String paymentType = matcher.group(4).trim();
            String date = matcher.group(5).trim();
            String time = matcher.group(6).trim();
            String merchant = matcher.group(7).trim();
            int accumulated = Integer.parseInt(matcher.group(8).replace(",", ""));

            return new CardSmsRecord(
                card, "승인", name, amount, paymentType, date, time, merchant, accumulated
            );
        } else {
            throw new ApplicationException("SMS 형식이 올바르지 않습니다.");
        }
    }
}
