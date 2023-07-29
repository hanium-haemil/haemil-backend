package com.haemil.backend.weather.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Slf4j
@Getter
@Entity
@NoArgsConstructor
@Table(name = "WeatherApi")
public class WeatherApi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weather_id")
    private Long id;

    @Column(nullable = false)
    private String fcstDate; // 예측일자

    @Column(nullable = false)
    private String fcstTime; // 예측시간

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String fcstValue; // 예보값

    @Builder
    public WeatherApi(String fcstDate, String fcstTime, String fcstValue, String category) {
        this.fcstDate = fcstDate;
        this.fcstTime = fcstTime;
        this.category = category;
        this.fcstValue = fcstValue;
    }
}
