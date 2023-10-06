package com.haemil.backend.prepare.controller;

import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.BaseResponse;
import com.haemil.backend.prepare.dto.*;
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
import javax.servlet.http.HttpServletRequest;

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

  private PrepareDto fetchDataAndProcess(HttpServletRequest request) throws BaseException {
    ResponseEntity<BaseResponse> weatherResponse = weatherController.sendGetRequest(request);

    List<WeatherInfoDto> todayTemps = weatherController.currentTimeData;
    Map<String, String> temps = weatherController.transformedData; // 최고 최저 온도

    ResponseEntity<BaseResponse> airResponse = airController.sendGetRequest(request);
    List<AirInfoDto> todayAirs = airController.infoList;

    ResponseEntity<BaseResponse> livingResponse = livingController.sendGetRequest(request);
    List<LivingInfoDto> todayLivings = livingController.infoList;

    PrepareDto prepareDto = new PrepareDto(todayTemps, todayAirs, temps, todayLivings);
    prepareService.filterWeatherData(todayTemps, prepareDto);
    prepareService.filterAirData(todayAirs, prepareDto);

    return prepareDto;
  }

  @GetMapping("/send")
  public ResponseEntity<BaseResponse> sendGetRequest(HttpServletRequest request) {
    try {
      PrepareDto prepareDto = fetchDataAndProcess(request);

      List<PrepareDto> prepareDtoList = new ArrayList<>();
      prepareDtoList.add(prepareDto);

      List<PrePareInfoDto> resultString = prepareService.ParsingJson(prepareDtoList);

      return new BaseResponse<>(resultString).convert();
    } catch (BaseException e) {
      return new BaseResponse<>(e.getStatus()).convert();
    }
  }

  @GetMapping("/weather")
  public ResponseEntity<BaseResponse> sendWeatherInfo(HttpServletRequest request) {
    try {
      PrepareDto prepareDto = fetchDataAndProcess(request);

      List<PrepareDto> prepareDtoList = new ArrayList<>();
      prepareDtoList.add(prepareDto);

      List<PrepareWeatherDto> resultString = prepareService.ParsingWeather(prepareDtoList);

      return new BaseResponse<>(resultString).convert();
    } catch (BaseException e) {
      return new BaseResponse<>(e.getStatus()).convert();
    }
  }

  @GetMapping("/need")
  public ResponseEntity<BaseResponse> sendNeedInfo(HttpServletRequest request) {
    try {
      PrepareDto prepareDto = fetchDataAndProcess(request);

      List<PrepareDto> prepareDtoList = new ArrayList<>();
      prepareDtoList.add(prepareDto);

      List<PrepareNeedInfoDto> resultString = prepareService.ParsingNeed(prepareDtoList);

      return new BaseResponse<>(resultString).convert();
    } catch (BaseException e) {
      return new BaseResponse<>(e.getStatus()).convert();
    }
  }
}
