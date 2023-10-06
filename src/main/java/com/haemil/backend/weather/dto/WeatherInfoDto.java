package com.haemil.backend.weather.dto;

import lombok.Data;

@Data
public class WeatherInfoDto {
  private String fcstDate; // 예측일자
  private String fcstTime; // 예측시간
  private String category; // 자료구분문자
  private String fcstValue; // 예보값
}
