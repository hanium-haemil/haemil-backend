package com.haemil.backend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorException {
    private ErrorCode errorCode;
}
