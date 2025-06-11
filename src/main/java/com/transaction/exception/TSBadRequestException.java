package com.transaction.exception;

import lombok.Getter;

@Getter
public class TSBadRequestException extends RuntimeException{
    private final String errorCode;

    public TSBadRequestException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
