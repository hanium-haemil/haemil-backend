package com.haemil.backend.global.exception;

import com.haemil.backend.global.config.ResponseStatus;

public class BaseException extends RuntimeException {

    private final ResponseStatus status;


    public BaseException(ResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    public ResponseStatus getStatus() {
        return status;
    }

}