package com.haemil.backend.prepare.dto;
import com.haemil.backend.weather.controller.WeatherController;
import com.haemil.backend.weather.controller.AirController;
import com.haemil.backend.weather.dto.AirInfoDto;
import com.haemil.backend.weather.dto.LivingInfoDto;
import com.haemil.backend.weather.dto.WeatherInfoDto;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class PrepareDto {
    private WeatherController weatherController;
    private AirController airController;
    private List<WeatherInfoDto> todayTemps;
    private List<AirInfoDto> todayAirs;
    private String maxTemp; // 오늘 최고 온도
    private String minTemp; // 오늘 최저 온도

    // 현재 실시간(시간별) weather 정보
    private String tmp; // 1시간 온도
    private String pop; // 강수 확률
    private String pty; // 강수 형태
    private String pcp; // 1시간 강수량
    private String reh; // 습도
    private String sky; // 하늘 상태

    // 대기질
    private String pm10grade; // 가장 최근꺼로 오늘꺼 미세먼지 등급
    private String pm25grade; // 가장 최근꺼로 오늘꺼 초미세먼지 등급

    // living
    private String feelLike; // 체감 온도
    private String uv; // 자외선

    // 기본 생성자
    public PrepareDto() {
    }

    // 필요한 데이터를 주입받는 생성자
    public PrepareDto(List<WeatherInfoDto> todayTemps, List<AirInfoDto> todayAirs, List<String> temps, List<LivingInfoDto> livings) {
        this.todayTemps = todayTemps;
        this.todayAirs = todayAirs;
        this.maxTemp = temps.get(1);
        this.minTemp = temps.get(0);

        if (!livings.isEmpty()) {
            this.feelLike = livings.get(0).getFeels_like();
            this.uv = livings.get(0).getUvi();
        }
    }
}
