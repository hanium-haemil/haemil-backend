package com.haemil.backend.weather.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Data
@Component
public class WeatherDto {
  private final String apiUrl =
      "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"; // 단기예보
  private final String numOfRows = "2000";
  private final String pageNo = "1";
  private final String dataType = "json";

  private final String base_date;
  private final String base_time = "0200";
  private final String current_time;
  private String nx;
  private String ny;

  public WeatherDto() {
    LocalDate today = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    this.base_date = today.format(formatter);

    // current_time - 지금 시 : ex) 12:30 -> 1200
    LocalTime currentTime = LocalTime.now();
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH");
    String formattedTime = currentTime.format(timeFormatter);

    this.current_time = formattedTime + "00";
  }
}
