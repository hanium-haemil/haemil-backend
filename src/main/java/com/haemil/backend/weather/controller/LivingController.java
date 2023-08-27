package com.haemil.backend.weather.controller;

import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.BaseResponse;
import com.haemil.backend.weather.dto.*;
import com.haemil.backend.weather.service.LivingService;
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
@RequestMapping("/living")
public class LivingController {
    private final LivingDto livingDto;
    private final LivingService livingService;
    public List<LivingInfoDto> infoList = null;

    @GetMapping("/send")
    public ResponseEntity<BaseResponse> sendGetRequest() {
        try {
            // feel like temp
            String jsonString1 = livingService.getLivingTempInfo(livingDto);
//            log.debug("jsonString : " + jsonString1);
            livingService.isJson(jsonString1);

            // uv
            String jsonString2 = livingService.getUVInfo(livingDto);
//            log.debug("jsonString : " + jsonString2);
            livingService.isJson(jsonString2);

            infoList = livingService.ParsingJson(jsonString1, jsonString2); // 전체 리스트
            return new BaseResponse<>(infoList).convert();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }
}
