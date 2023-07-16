package com.haemil.backend.weather.controller;

import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.BaseResponse;
import com.haemil.backend.weather.dto.WeatherDto;
import com.haemil.backend.weather.dto.WeatherInfoDto;
import com.haemil.backend.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/weather")
public class WeatherController {
    private final WeatherDto weatherDto;
    private final WeatherService weatherService;

    @GetMapping("/send")
    public ResponseEntity<BaseResponse> sendGetRequest() {
        try {
            String jsonString = weatherService.getWeatherInfo(weatherDto);
            log.debug("jsonString : " + jsonString);
            weatherService.isJson(jsonString);

            List<WeatherInfoDto> infoList = weatherService.ParsingJson(jsonString);
            return new BaseResponse<>(infoList).convert();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }
}
