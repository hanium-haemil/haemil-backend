package com.haemil.backend.global.config;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseStatus {

    // --- 4xx Client Error ---
    BAD_REQUEST(400, HttpStatus.Series.CLIENT_ERROR, "Bad Request"),
    UNAUTHORIZED(401,HttpStatus.Series.CLIENT_ERROR, "Unauthorized"),
    FORBIDDEN(403,HttpStatus.Series.CLIENT_ERROR, "Forbidden"),
    NOT_FOUND(404,HttpStatus.Series.CLIENT_ERROR, "Not Found"),
    METHOD_NOT_ALLOWED(405,HttpStatus.Series.CLIENT_ERROR, "Method Not Allowed"),
    REQUEST_TIMEOUT(408,HttpStatus.Series.CLIENT_ERROR, "Request Timeout"),
    CONFLICT(409,HttpStatus.Series.CLIENT_ERROR, "Conflict"),
    PAYLOAD_TOO_LARGE(413,HttpStatus.Series.CLIENT_ERROR, "Payload Too Large"),
    URI_TOO_LONG(414,HttpStatus.Series.CLIENT_ERROR, "URI Too Long"),

    // --- 5xx Server Error ---
    INTERNAL_SERVER_ERROR(500,HttpStatus.Series.SERVER_ERROR, "Internal Server Error"),
    BAD_GATEWAY(502,HttpStatus.Series.SERVER_ERROR, "Bad Gateway"),
    GATEWAY_TIMEOUT(504,HttpStatus.Series.SERVER_ERROR, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505,HttpStatus.Series.SERVER_ERROR, "HTTP Version not supported");

    private boolean success;
    private int status;
    private String message;

    private ResponseStatus(boolean success, int code, String message) {
        this.success = success;
        this.status = status;
        this.message = message;
    }

    private ResponseStatus(int code, HttpStatus.Series series, String message) {
        this.success = false;
        this.status = status;
        this.message = message;
    }
}
