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
import java.util.List;

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
    @GetMapping("/send")
    public ResponseEntity<BaseResponse> sendGetRequest() {
        try {
            String jsonString = weatherService.getWeatherInfo(weatherDto);
            log.debug("jsonString : " + jsonString);
            weatherService.isJson(jsonString);

            List<WeatherInfoDto> infoList = weatherService.ParsingJson(jsonString); // 전체 리스트

            // fcstDate가 오늘이고 fcstTime이 current_time인 경우의 데이터들을 받아내기
            currentTimeData = weatherService.filterCurrentTimeData(infoList, weatherDto);
            log.info("currentTimeData = {}", currentTimeData);

            // fcstDate가 지정된 날짜인 경우의 데이터들을 받아내기
//            List<WeatherInfoDto> specifiedDateData = weatherService.filterSpecifiedDateData(infoList, weatherDto);
//            log.info("specifiedDateData = {}", specifiedDateData);

            // fcstDate가 오늘이고 category가 "TMN" 또는 "TMX"인 경우의 데이터들을 받아내기
            tmnAndTmxData = weatherService.filterTMNandTMXData(infoList, weatherDto);
            log.info("tmnAndTmxData = {}", tmnAndTmxData);

            // 필요에 따라 결과를 가공하거나 반환하십시오.
            return new BaseResponse<>(infoList).convert();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }
}
