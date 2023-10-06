package com.haemil.backend.prepare.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class PrepareWeatherDto {
  private String pm10value; // 가장 최근꺼로 오늘꺼 미세먼지 수치
  private String pm25value; // 가장 최근꺼로 오늘꺼 미세먼지 수치

  private String pop; // 강수 확률
  private String pcp; // 1시간 강수량

  private String wsd; // 풍속(m/s)
  private String uv; // 자외선
}
