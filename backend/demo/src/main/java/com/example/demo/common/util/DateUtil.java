package com.example.demo.common.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class DateUtil {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");





    /**
     * 특정 타임존의 LocalDate 기준 하루 전체를 UTC 기준 LocalDateTime 범위로 반환
     *
     * @param date LocalDate 기준 (예: KST 기준 오늘)
     * @param zone 타임존 (예: ZoneId.of("Asia/Seoul"))
     * @return LocalDateTime[] { UTC 시작, UTC 끝 }
     */
    public static LocalDateTime[] getUtcRangeForDate(LocalDate date, ZoneId zone) {
        ZonedDateTime start = date.atStartOfDay(zone);
        ZonedDateTime end = date.plusDays(1).atStartOfDay(zone).minusNanos(1);

        LocalDateTime utcStart = start.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
        LocalDateTime utcEnd = end.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();

        return new LocalDateTime[]{utcStart, utcEnd};
    }

    /**
     * 특정 날짜가 속한 주의 시작과 종료 반환
     *
     * @param date 기준 날짜
     * @return List<LocalDateTime> -> 시작, 끝
     */
    public static List<LocalDateTime> getWeekRange(LocalDate date) {

        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = date.with(DayOfWeek.SUNDAY);

        LocalDateTime startDateTime = startOfWeek.atStartOfDay();
        LocalDateTime endDateTime = endOfWeek.atTime(LocalTime.MAX);

        List<LocalDateTime> weekRange = new ArrayList<>();

        weekRange.add(startDateTime);
        weekRange.add(endDateTime);

        return weekRange;
    }

    /**
     * 특정 날짜가 속한 달의 시작과 종료 반환
     *
     * @param date 기준 날짜
     * @return List<LocalDateTime> -> 시작, 끝
     */
    public static List<LocalDateTime> getMonthRange(LocalDate date) {

        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        LocalDate lastDayOfMonth = date.withDayOfMonth(date.lengthOfMonth());

        LocalDateTime startDateTime = firstDayOfMonth.atStartOfDay();
        LocalDateTime endDateTime = lastDayOfMonth.atTime(LocalTime.MAX);

        List<LocalDateTime> monthRange = new ArrayList<>();

        monthRange.add(startDateTime);
        monthRange.add(endDateTime);

        return monthRange;
    }

    /** KST 기준 LocalDateTime → UTC LocalDateTime 변환 */
    public static LocalDateTime kstToUtc(LocalDateTime kstTime) {
        ZonedDateTime zdt = kstTime.atZone(KST);
        return zdt.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    /** UTC 기준 LocalDateTime → KST LocalDateTime 변환 */
    public static LocalDateTime utcToKst(LocalDateTime utcTime) {
        ZonedDateTime zdt = utcTime.atZone(ZoneOffset.UTC);
        return zdt.withZoneSameInstant(KST).toLocalDateTime();
    }
}
