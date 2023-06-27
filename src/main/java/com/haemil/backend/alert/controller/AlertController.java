package com.haemil.backend.alert.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.haemil.backend.alert.dto.ApiInfoDto;
import com.haemil.backend.alert.dto.GetApiDto;
import com.haemil.backend.alert.service.AlertService;
import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.BaseResponse;
import com.haemil.backend.global.config.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/alert")
public class AlertController {

    private final AlertService alertService;
    private final GetApiDto getApiDto;

    @GetMapping("/send")
    public ResponseEntity<BaseResponse> sendGetRequest() {

        try {
            String jsonString = alertService.getAlertInfo(getApiDto);
            // SERVICE ERROR 일때 무한로딩. error handling 필요.
            log.debug("jsonString: "+jsonString);
            alertService.isJson(jsonString);

            List<ApiInfoDto> infoList  = alertService.ParsingJson(jsonString);
            return new BaseResponse<>(infoList).convert();
        } catch (BaseException e){
            // 실패시 custom한 status로 code 헤더 설정, body로 메세지 반환
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }
}

