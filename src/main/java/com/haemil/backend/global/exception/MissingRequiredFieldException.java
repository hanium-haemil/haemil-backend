package com.haemil.backend.global.exception;

import com.haemil.backend.global.config.ResponseStatus;

public class MissingRequiredFieldException extends BaseException {

    public MissingRequiredFieldException(String s) {
        super(ResponseStatus.MISSING_REQUIRED_FIELD);
    }

}
