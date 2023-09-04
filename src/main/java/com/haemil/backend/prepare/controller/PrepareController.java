package com.haemil.backend.prepare.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.BaseResponse;
import com.haemil.backend.global.config.ResponseStatus;
import com.haemil.backend.prepare.dto.PrePareInfoDto;
import com.haemil.backend.prepare.dto.PrepareDto;
import com.haemil.backend.prepare.dto.PrepareNeedInfoDto;
import com.haemil.backend.prepare.dto.PrepareWeatherDto;
import com.haemil.backend.prepare.service.PrepareService;
import com.haemil.backend.weather.controller.AirController;
import com.haemil.backend.weather.controller.LivingController;
import com.haemil.backend.weather.controller.WeatherController;
import com.haemil.backend.weather.dto.AirInfoDto;
import com.haemil.backend.weather.dto.LivingInfoDto;
import com.haemil.backend.weather.dto.WeatherInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/prepare")
public class PrepareController {
    private final PrepareService prepareService;
    private final WeatherController weatherController;
    private final LivingController livingController;
    private final AirController airController;
    private final ObjectMapper objectMapper; // ObjectMapper 주입

    private PrepareDto fetchDataAndProcess() throws BaseException {
        ResponseEntity<BaseResponse> weatherResponse = weatherController.sendGetRequest();
        List<WeatherInfoDto> todayTemps = weatherController.currentTimeData;

        ResponseEntity<BaseResponse> airResponse = airController.sendGetRequest();
        List<AirInfoDto> todayAirs = airController.infoList;

        ResponseEntity<BaseResponse> livingResponse = livingController.sendGetRequest();
        List<LivingInfoDto> todayLivings = livingController.infoList;

        Map<String, String> temps = weatherController.transformedData; // 최고 최저 온도

        PrepareDto prepareDto = new PrepareDto(todayTemps, todayAirs, temps, todayLivings);
        prepareService.filterWeatherData(todayTemps, prepareDto);
        prepareService.filterAirData(todayAirs, prepareDto);

        return prepareDto;
    }

    @GetMapping("/send")
    public ResponseEntity<BaseResponse> sendGetRequest() {
        try {
            PrepareDto prepareDto = fetchDataAndProcess();

            List<PrepareDto> prepareDtoList = new ArrayList<>();
            prepareDtoList.add(prepareDto);

            List<PrePareInfoDto> resultString = prepareService.ParsingJson(prepareDtoList);
//            log.info("prePare_result = {}", resultString); // 외출 적합도 결과 log

            return new BaseResponse<>(resultString).convert();
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }

    @GetMapping("/weather")
    public ResponseEntity<BaseResponse> sendWeatherInfo() {
        try {
            PrepareDto prepareDto = fetchDataAndProcess();

            List<PrepareDto> prepareDtoList = new ArrayList<>();
            prepareDtoList.add(prepareDto);

            List<PrepareWeatherDto> resultString = prepareService.ParsingWeather(prepareDtoList);
//            log.info("prePare_weather_result = {}", resultString); // 외출 - 날씨 관련 정보

            return new BaseResponse<>(resultString).convert();
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }

    @GetMapping("/need")
    public ResponseEntity<BaseResponse> sendNeedInfo() {
        try {
            PrepareDto prepareDto = fetchDataAndProcess();

            List<PrepareDto> prepareDtoList = new ArrayList<>();
            prepareDtoList.add(prepareDto);

            List<PrepareNeedInfoDto> resultString = prepareService.ParsingNeed(prepareDtoList);
//            log.info("prePare_need_result = {}", resultString); // 외출 물품 정보

            return new BaseResponse<>(resultString).convert();
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }
}
