package com.haemil.backend.prepare.dto;

import com.haemil.backend.weather.controller.WeatherController;
import com.haemil.backend.weather.controller.AirController;
import com.haemil.backend.weather.dto.AirInfoDto;
import com.haemil.backend.weather.dto.LivingInfoDto;
import com.haemil.backend.weather.dto.WeatherInfoDto;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
  private String wsd; // 풍속(m/s)

  // 대기질
  private String pm10grade; // 가장 최근꺼로 오늘꺼 미세먼지 등급
  private String pm10value; // 가장 최근꺼로 오늘꺼 미세먼지 수치
  private String pm25grade; // 가장 최근꺼로 오늘꺼 초미세먼지 등급
  private String pm25value; // 가장 최근꺼로 오늘꺼 미세먼지 수치

  // living
  private String feelLike; // 체감 온도
  private String uv; // 자외선

  public PrepareDto(
      List<WeatherInfoDto> todayTemps,
      List<AirInfoDto> todayAirs,
      Map<String, String> temps,
      List<LivingInfoDto> livings) {
    this.todayTemps = todayTemps;
    this.todayAirs = todayAirs;
    String maxTemp = temps.get("max");
    String minTemp = temps.get("min");
    this.maxTemp = maxTemp;
    this.minTemp = minTemp;

    if (!livings.isEmpty()) {
      this.feelLike = livings.get(0).getFeels_like();
      this.uv = livings.get(0).getUvi();
    }
  }
}
