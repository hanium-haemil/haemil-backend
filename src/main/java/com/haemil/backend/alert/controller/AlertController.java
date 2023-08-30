package com.haemil.backend.alert.controller;

import com.haemil.backend.alert.dto.AlertDto;
import com.haemil.backend.alert.dto.GetApiDto;
import com.haemil.backend.alert.dto.ReqCoordDto;
import com.haemil.backend.alert.service.AlertService;
import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/alert")
public class AlertController {

    private final AlertService alertService;
    private final GetApiDto getApiDto;

    @PostMapping("/send")
    public ResponseEntity<BaseResponse> sendGetRequest(@RequestBody ReqCoordDto reqCoordDto) {

        try {
            String jsonString = alertService.getAlertInfo(getApiDto);
            alertService.isJson(jsonString);
            List<AlertDto> alertApiList = alertService.ParsingJson(jsonString, reqCoordDto);
            return new BaseResponse<>(alertApiList).convert();
        } catch (BaseException e){
            // 실패시 custom한 status로 code 헤더 설정, body로 메세지 반환
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }
}