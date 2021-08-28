package com.kkwonsy.trp.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INTERNAL_ERROR(500, "internal error"),
    MEMBER_NOT_FOUND(404, "member's not found"),
    CITY_NOT_FOUND(404, "city's not found"),
    TRIP_NOT_FOUND(404, "trip's not found"),
    CITY_ALREADY_EXIST(400, "city already exists"),
    TRIP_ALREADY_EXIST(400, "the trip which consists of memberId and cityId already exists"),
    TRIP_INVALID_DATE(400, "the trip date is invalid"),
    ;

    private Integer httpStatus;
    private String message;


    ErrorCode(Integer httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
