package com.haemil.backend.weather.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class LivingInfoDto {
    private String feels_like; // 체감 온도
    private String uvi; // 자외선 수치
}
