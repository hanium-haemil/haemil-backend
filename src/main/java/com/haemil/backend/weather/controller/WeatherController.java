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

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RestController
@RequiredArgsConstructor
@RequestMapping("/weather")
public class WeatherController {
    private final WeatherService weatherService;

    public Map<String, String> transformedData;
    public List<WeatherInfoDto> currentTimeData;

    private WeatherDto fetchDataAndProcess(HttpServletRequest request) throws BaseException {
        String latitude = request.getParameter("latitude");
        String longitude = request.getParameter("longitude");

        double lat = Double.parseDouble(latitude);
        double lon = Double.parseDouble(longitude);

        latitude = String.format("%.0f", lat);
        longitude = String.format("%.0f", lon);

        WeatherDto weatherDto = new WeatherDto();
        weatherDto.setNx(latitude);
        weatherDto.setNy(longitude);

        return weatherDto;
    }

    @GetMapping("/today")
    public ResponseEntity<BaseResponse> getTodayWeather(HttpServletRequest request) { // 오늘 현재 시간대
        try {
            WeatherDto weatherDto = fetchDataAndProcess(request);

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
    public ResponseEntity<BaseResponse> sendGetRequest(HttpServletRequest request) { // 모든 날씨 정보
        try {
            WeatherDto weatherDto = fetchDataAndProcess(request);

            String jsonString = weatherService.getWeatherInfo(weatherDto);
            weatherService.isJson(jsonString);
            List<WeatherInfoDto> infoList = weatherService.ParsingJson(jsonString);

            currentTimeData = weatherService.filterCurrentTimeData(infoList, weatherDto);
            transformedData = weatherService.transformWeatherData(infoList, currentTimeData, weatherDto);

            return new BaseResponse<>(infoList).convert();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }

    @GetMapping("/data")
    public ResponseEntity<BaseResponse> getTMNandTMXData(HttpServletRequest request) {
        try {
            WeatherDto weatherDto = fetchDataAndProcess(request);

            String jsonString = weatherService.getWeatherInfo(weatherDto);
            weatherService.isJson(jsonString);
            List<WeatherInfoDto> infoList = weatherService.ParsingJson(jsonString);
            List<WeatherInfoDto> todayData = weatherService.filterCurrentTimeData(infoList, weatherDto);

            transformedData = weatherService.transformWeatherData(infoList, todayData, weatherDto);

            return new BaseResponse<>(transformedData).convert();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }

    @GetMapping("/tmps")
    public ResponseEntity<BaseResponse> getTemperatures(HttpServletRequest request) { // 3일치 온도&하늘상태 가져오기
        try {
            WeatherDto weatherDto = fetchDataAndProcess(request);

            String jsonString = weatherService.getWeatherInfo(weatherDto);
            weatherService.isJson(jsonString);
            List<WeatherInfoDto> infoList = weatherService.ParsingJson(jsonString);
            List<Map<String, String>> temperatureData = weatherService.filterCurrentTimeAndSpecifiedDateData(infoList, "1500");

            return new BaseResponse<>(temperatureData).convert();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }

    @GetMapping("/times")
    public ResponseEntity<BaseResponse> getNextHoursWeather(HttpServletRequest request) { // 시간대별 온도&하늘상태 가져오기
        try {
            WeatherDto weatherDto = fetchDataAndProcess(request);

            String jsonString = weatherService.getWeatherInfo(weatherDto);
            weatherService.isJson(jsonString);
            List<WeatherInfoDto> infoList = weatherService.ParsingJson(jsonString);

            // 현재 날짜를 기준으로 시작 시간 설정
            LocalDate currentDate = LocalDate.now();
            LocalTime startTime = LocalTime.of(8, 0); // 시작 시간을 8시로 설정

            // 오늘의 데이터를 가져오도록 설정
            List<Map<String, String>> filteredData = weatherService.filterNextData(infoList, startTime, 15);

            return new BaseResponse<>(filteredData).convert();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }



}

