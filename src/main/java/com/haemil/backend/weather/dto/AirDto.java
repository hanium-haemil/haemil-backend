package com.haemil.backend.weather.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class AirDto {
    private final String apiUrl = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"; // 측정소별 실시간 측정정보 조회
    private final String serviceKey = "Zj0sLbMrwO6sNx5VVTO1ExwsGaiiab7foNhOtvUgNrl/S2AeCLt1o2B4EzZEaYBg/OsM0vkFjuhttPUq3vbq6A==";
    private final String numOfRows = "10";
    private final String pageNo = "1";
    private final String returnType = "json";
    private final String stationName = "종로구"; // 임의 지정
    private final String dataTerm = "DAILY";
    private final String ver = "1.0";
}
