package com.haemil.backend.prepare.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.BaseResponse;
import com.haemil.backend.global.config.ResponseStatus;
import com.haemil.backend.prepare.dto.PrepareDto;
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

    @GetMapping("/send")
    public ResponseEntity<BaseResponse> sendGetRequest() {
        try {
            // Call /weather/send to get weather data
            ResponseEntity<BaseResponse> weatherResponse = weatherController.sendGetRequest();
            List<WeatherInfoDto> todayTemps = weatherController.currentTimeData;

            // Call /air/send to get air data
            ResponseEntity<BaseResponse> airResponse = airController.sendGetRequest();
            List<AirInfoDto> todayAirs = airController.infoList;

            ResponseEntity<BaseResponse> livingResponse = livingController.sendGetRequest();
            List<LivingInfoDto> todayLivings = livingController.infoList;

            List<String> temps = weatherController.tmnAndTmxData;
//            log.info("minmax = {}", temps.get(0));

            PrepareDto prepareDto = new PrepareDto(todayTemps, todayAirs, temps, todayLivings);
            prepareService.filterWeatherData(todayTemps, prepareDto);
//            log.info("tmp = {}", prepareDto.getTmp());

            prepareService.filterAirData(todayAirs, prepareDto);

            List<PrepareDto> prepareDtoList = new ArrayList<>();
            prepareDtoList.add(prepareDto);

            String resultString = prepareService.ParsingJson(prepareDtoList);
            log.info("prePare_result = {}", resultString);

            Object jsonResult = objectMapper.readValue(resultString, Object.class);
            String prettyJsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonResult);

            return new BaseResponse<>(prettyJsonString).convert();
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus()).convert();
        } catch (JsonProcessingException e) {
            return new BaseResponse<>(ResponseStatus.CANNOT_CONVERT_JSON).convert();
        }
    }
}

