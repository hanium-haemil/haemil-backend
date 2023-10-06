package com.haemil.backend.weather.controller;

import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.BaseResponse;
import com.haemil.backend.weather.dto.AirDto;
import com.haemil.backend.weather.dto.AirInfoDto;
import com.haemil.backend.weather.dto.TransferDto;
import com.haemil.backend.weather.service.AirService;
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
@RequestMapping("/air")
public class AirController {
  private final AirDto airdto;
  private final AirService airService;

  public List<AirInfoDto> infoList = null;

  public TransferDto fetchDataAndProcess(HttpServletRequest request) throws BaseException {
    String latitude = request.getParameter("latitude");
    String longitude = request.getParameter("longitude");

    TransferDto transferDto = new TransferDto();
    transferDto.setX(latitude);
    transferDto.setY(longitude);

    return transferDto;
  }

  @GetMapping("/send")
  public ResponseEntity<BaseResponse> sendGetRequest(HttpServletRequest request) {
    try {
      TransferDto transferDto = fetchDataAndProcess(request);
      String jsonString = airService.getAirInfo(airdto, transferDto);

      airService.isJson(jsonString);

      infoList = airService.ParsingJson(jsonString);
      return new BaseResponse<>(infoList).convert();
    } catch (BaseException e) {
      return new BaseResponse<>(e.getStatus()).convert();
    }
  }
}
