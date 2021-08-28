package com.kkwonsy.trp.model;

import org.springframework.util.StringUtils;

import com.kkwonsy.trp.exception.ErrorCode;

import lombok.Getter;

@Getter
public class ApiResponse {

    private Boolean isSuccess;
    private Integer errorCode;
    private String errorMessage;

    public void success() {
        isSuccess = true;
    }

    public static ApiResponse ok() {
        ApiResponse response = new ApiResponse();
        response.isSuccess = true;
        return response;
    }

    public static ApiResponse error(ErrorCode errorCode, String message) {
        ApiResponse response = new ApiResponse();
        response.errorCode = errorCode.getHttpStatus();
        response.errorMessage = StringUtils.hasText(message) ? message : errorCode.getMessage();
        return response;
    }
}
