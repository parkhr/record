package com.example.demo.economy.domain;

import com.example.demo.common.exception.ApplicationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardSmsParser {

    public static CardSmsRecord parse(String message) {
        Pattern 사용금액Pattern = Pattern.compile("사용금액\\s*:\\s*([\\d,]+)원");
        Pattern 이용카드Pattern = Pattern.compile("이용카드\\s*:\\s*([^\\-]+)");
        Pattern 거래종류Pattern = Pattern.compile("거래종류\\s*:\\s*([^\\-]+)");
        Pattern 거래구분Pattern = Pattern.compile("거래구분\\s*:\\s*([^\\-]+)");
        Pattern 가맹점명Pattern = Pattern.compile("가맹점명\\s*:\\s*([^\\-]+)");
        Pattern 거래시간Pattern = Pattern.compile("거래시간\\s*:\\s*(\\d{2}/\\d{2})\\s+(\\d{2}:\\d{2})");
        Pattern 누적금액Pattern = Pattern.compile("누적금액\\s*:\\s*([\\d,]+)원");

        Matcher m1 = 사용금액Pattern.matcher(message);
        Matcher m2 = 이용카드Pattern.matcher(message);
        Matcher m3 = 거래종류Pattern.matcher(message);
        Matcher m4 = 거래구분Pattern.matcher(message);
        Matcher m5 = 가맹점명Pattern.matcher(message);
        Matcher m6 = 거래시간Pattern.matcher(message);
        Matcher m7 = 누적금액Pattern.matcher(message);

        if (m1.find() && m2.find() && m3.find() && m4.find() && m5.find() && m6.find() && m7.find()) {
            int amount = Integer.parseInt(m1.group(1).replace(",", ""));
            String card = m2.group(1).trim();
            String type = m3.group(1).trim();
            String payType = m4.group(1).trim();
            String merchant = m5.group(1).trim();
            String date = m6.group(1).trim();
            String time = m6.group(2).trim();
            int accumulated = Integer.parseInt(m7.group(1).replace(",", ""));

            return new CardSmsRecord(
                card,
                "승인",
                "박*렬", // 혹은 추후 파싱 처리
                amount,
                payType,
                date,
                time,
                merchant,
                accumulated
            );
        } else {
            throw new ApplicationException("하나카드 MESSAGE 형식이 올바르지 않습니다.");
        }
    }
}
