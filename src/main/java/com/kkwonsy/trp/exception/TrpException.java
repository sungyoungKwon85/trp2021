package com.kkwonsy.trp.exception;

public class TrpException extends Exception {

    private final ErrorCode errorCode;

    public TrpException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public TrpException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public TrpException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public TrpException(Throwable cause, ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public TrpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
        ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
