package com.haemil.backend.weather.controller;

import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.BaseResponse;
import com.haemil.backend.weather.dto.AirDto;
import com.haemil.backend.weather.dto.AirInfoDto;
import com.haemil.backend.weather.service.AirService;
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
@RequestMapping("/air")
public class AirController {
    private final AirDto airdto;
    private final AirService airService;

    public List<AirInfoDto> infoList = null;
    @GetMapping("/send")
    public ResponseEntity<BaseResponse> sendGetRequest() {
        try {
            String jsonString = airService.getAirInfo(airdto);
            log.debug("Air - jsonString : " + jsonString);
            airService.isJson(jsonString);

            infoList = airService.ParsingJson(jsonString);
            return new BaseResponse<>(infoList).convert();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }
}
