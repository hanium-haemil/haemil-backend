package com.haemil.backend.weather.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class StationDto {
  private final String apiUrl =
      "http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList";
  private final String returnType = "json";

  private String tmX;
  private String tmY;
}
