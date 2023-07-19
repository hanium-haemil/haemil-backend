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
@Table(name = "AirApi")
public class AirApi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "air_id")
    private Long id;

    @Column(nullable = false)
    private String dataTime; // 통보시간

    @Column(nullable = false)
    private String pm10Value; // 미세먼지 농도

    @Column(nullable = false)
    private String pm25Value; // 초미세먼지 농도

    @Column(nullable = false)
    private String pm10Grade; // 미세먼지 24시간 등급

    @Column(nullable = false)
    private String pm25Grade; // 초미세먼지 농도

    @Builder
    public AirApi(String dataTime, String pm10Value, String pm10Grade, String pm25Value, String pm25Grade) {
        this.dataTime = dataTime;
        this.pm10Grade = pm10Grade;
        this.pm10Value = pm10Value;
        this.pm25Grade = pm25Grade;
        this.pm25Value = pm25Value;
    }
}
