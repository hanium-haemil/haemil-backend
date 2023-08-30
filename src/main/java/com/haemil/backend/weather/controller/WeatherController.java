package com.haemil.backend.weather.controller;

import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.BaseResponse;
import com.haemil.backend.weather.dto.WeatherDto;
import com.haemil.backend.weather.dto.WeatherInfoDto;
import com.haemil.backend.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RestController
@RequiredArgsConstructor
@RequestMapping("/weather")
public class WeatherController {
    private final WeatherDto weatherDto;
    private final WeatherService weatherService;

    public List<String> tmnAndTmxData;
    public List<WeatherInfoDto> currentTimeData;
    @GetMapping("/today")
    public ResponseEntity<BaseResponse> getTodayWeather() { // 오늘 현재 시간대
        try {
            String jsonString = weatherService.getWeatherInfo(weatherDto);
            weatherService.isJson(jsonString);
            List<WeatherInfoDto> infoList = weatherService.ParsingJson(jsonString);
            List<WeatherInfoDto> todayData = weatherService.filterCurrentTimeData(infoList, weatherDto);
            return new BaseResponse<>(todayData).convert();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }

    @GetMapping("/send")
    public ResponseEntity<BaseResponse> sendGetRequest() { // 모든 날씨 정보
        try {
            String jsonString = weatherService.getWeatherInfo(weatherDto);
            weatherService.isJson(jsonString);
            List<WeatherInfoDto> infoList = weatherService.ParsingJson(jsonString);
            currentTimeData = weatherService.filterCurrentTimeData(infoList, weatherDto);
            tmnAndTmxData = weatherService.filterTMNandTMXData(infoList, weatherDto);

            return new BaseResponse<>(infoList).convert();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }

    @GetMapping("/tmps")
    public ResponseEntity<BaseResponse> getTemperatures() {
        try {
            String jsonString = weatherService.getWeatherInfo(weatherDto);
            weatherService.isJson(jsonString);
            List<WeatherInfoDto> infoList = weatherService.ParsingJson(jsonString);
            List<WeatherInfoDto> temperatureData = weatherService.filterCurrentTimeAndSpecifiedDateData(infoList, "1500");

            return new BaseResponse<>(temperatureData).convert();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }



}

