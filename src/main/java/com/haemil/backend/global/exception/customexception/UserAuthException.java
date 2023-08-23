package com.haemil.backend.global.exception.customexception;


import com.haemil.backend.global.exception.CustomException;

public class UserAuthException extends CustomException {

    public UserAuthException(String message) {
        super(message);
    }
}
