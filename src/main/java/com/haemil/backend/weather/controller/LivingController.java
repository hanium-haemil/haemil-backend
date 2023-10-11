package com.haemil.backend.weather.controller;

import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.BaseResponse;
import com.haemil.backend.weather.dto.*;
import com.haemil.backend.weather.service.LivingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Component
@RestController
@RequiredArgsConstructor
@RequestMapping("/living")
public class LivingController {
  private final LivingService livingService;
  public List<LivingInfoDto> infoList = null;

  private LivingDto fetchDataAndProcess(HttpServletRequest request) throws BaseException {
    String latitude = request.getParameter("latitude");
    String longitude = request.getParameter("longitude");

    double lat = Double.parseDouble(latitude);
    double lon = Double.parseDouble(longitude);

    latitude = String.format("%.0f", lat);
    longitude = String.format("%.0f", lon);

    LivingDto livingDto = new LivingDto();
    livingDto.setLon(longitude);
    livingDto.setLat(latitude);

    return livingDto;
  }

  @GetMapping("/send")
  public ResponseEntity<BaseResponse> sendGetRequest(HttpServletRequest request) {
    try {
      LivingDto livingDto = fetchDataAndProcess(request);

      // feel like temp
      String jsonString1 = livingService.getLivingTempInfo(livingDto);
      livingService.isJson(jsonString1);

      // uv
      String jsonString2 = livingService.getUVInfo(livingDto, request);
      livingService.isJson(jsonString2);

      infoList = livingService.ParsingJson(jsonString1, jsonString2); // 전체 리스트
      return new BaseResponse<>(infoList).convert();
    } catch (BaseException e) {
      return new BaseResponse<>(e.getStatus()).convert();
    }
  }
}
