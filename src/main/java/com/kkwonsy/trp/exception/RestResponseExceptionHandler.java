package com.kkwonsy.trp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.kkwonsy.trp.model.ApiResponse;

@RestControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {Exception.class, RuntimeException.class})
    public ResponseEntity<ApiResponse> handleException(Exception ex) {
        return new ResponseEntity(
            ApiResponse.error(ErrorCode.INTERNAL_ERROR, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {TrpException.class})
    public ResponseEntity<ApiResponse> handleException(TrpException ex) {
        ApiResponse response = ApiResponse.error(ex.getErrorCode(), ex.getMessage());
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex.getErrorCode().getHttpStatus() == HttpStatus.NOT_FOUND.value()) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (ex.getErrorCode().getHttpStatus() == HttpStatus.BAD_REQUEST.value()) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else {
            response = ApiResponse.error(ErrorCode.INTERNAL_ERROR, ex.getMessage());
        }

        return new ResponseEntity(response, httpStatus);
    }
}
