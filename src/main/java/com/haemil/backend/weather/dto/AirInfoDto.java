package com.haemil.backend.weather.dto;

import lombok.Data;

@Data
public class AirInfoDto {
    private String dataTime; // 측정일
    private String pm10Value; // 미세먼지 농도
    private String pm25Value; // 초미세먼지 농도
    private String pm10Grade; // 미세먼지 24시간 등급
    private String pm25Grade; // 초미세먼지 농도
}
