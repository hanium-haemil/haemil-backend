package com.haemil.backend.weather.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@Component
public class WeatherDto {
    private final String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"; // 단기예보
    private final String serviceKey = "Zj0sLbMrwO6sNx5VVTO1ExwsGaiiab7foNhOtvUgNrl/S2AeCLt1o2B4EzZEaYBg/OsM0vkFjuhttPUq3vbq6A==";
    private final String numOfRows = "10";
    private final String pageNo = "1";
    private final String dataType ="json";
    LocalDate today = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    String formattedDate = today.format(formatter);
    private final String base_date = formattedDate; // 20230626 형식
    private final String base_time = "0500";
    private final String nx = "55";
    private final String ny = "127";
}
