package com.haemil.backend.weather.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Component
public class LivingDto {
    // 체감 온도
    private final String apiUrl = "https://api.openweathermap.org/data/2.5/weather";
    private String lat;
    private String lon;
    private final String lang = "kr";
    private final String units = "metric";

    // 자외선
    private final String apiUrlUV = "http://apis.data.go.kr/1360000/LivingWthrIdxServiceV4/getUVIdxV4";
    private String areaNo; // 행정 구역 코드
    private final String numOfRows = "10";
    private final String pageNo = "1";
    private final String dataType = "json";
    private final String time = generateFormattedTime(); // 현재 시간으로 변경

    private String generateFormattedTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        return now.format(formatter);
    }
}
