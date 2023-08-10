package com.haemil.backend.map.controller;

import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.BaseResponse;
import com.haemil.backend.map.service.MapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MapController {
    private final MapService mapService;

    @GetMapping("/map")
    public ResponseEntity<BaseResponse> getMapUrl(@RequestParam("location") String location) {
        try {
            String mapUrlString = mapService.getMapUrl(location);
            return new BaseResponse<>(mapUrlString).convert();
        } catch (BaseException e){
            // 실패시 custom한 status로 code 헤더 설정, body로 메세지 반환
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }
}
