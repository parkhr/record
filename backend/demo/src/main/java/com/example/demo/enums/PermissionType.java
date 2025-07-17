package com.example.demo.enums;

public enum PermissionType {

    // 기록물관리
    WRITE_RECORD,
    READ_RECORD,
    UPDATE_RECORD,
    DELETE_RECORD,

    // 게층관리
    WRITE_LAYER,
    READ_LAYER,
    UPDATE_LAYER,
    DELETE_LAYER,

    // 서고관리
    MANAGE_STORAGE,

    // 예약
    RESERVATION,

    // 통계
    READ_STATISTIC,

    // 정수점검
    MANAGE_CHECK,

    // 권한그룹관리
    WRITE_ROLE,
    READ_ROLE,
    UPDATE_ROLE,
    DELETE_ROLE,

    // 메뉴관리
    WRITE_MENU,
    READ_MENU,
    UPDATE_MENU,
    DELETE_MENU,

    //관리자
    WRITE_ADMIN,
    READ_ADMIN,
    UPDATE_ADMIN,
    DELETE_ADMIN,

    // 알림관리
    WRITE_ALARM,
    READ_ALARM,
    UPDATE_ALARM,
    DELETE_ALARM,
}
