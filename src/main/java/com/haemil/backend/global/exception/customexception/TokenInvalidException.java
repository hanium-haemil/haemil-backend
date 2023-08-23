package com.haemil.backend.global.exception.customexception;


import com.haemil.backend.global.exception.CustomException;

public class TokenInvalidException extends CustomException {
    public TokenInvalidException(String message) {
        super(message);
    }
}
