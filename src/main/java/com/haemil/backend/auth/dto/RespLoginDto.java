package com.haemil.backend.auth.dto;

import org.springframework.http.HttpHeaders;

public class RespLoginDto {
    private HttpHeaders headers;
    private LoginDto loginDto;

    public RespLoginDto(HttpHeaders headers, LoginDto loginDto) {
        this.headers = headers;
        this.loginDto = loginDto;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public LoginDto getLoginDto() {
        return loginDto;
    }
}
