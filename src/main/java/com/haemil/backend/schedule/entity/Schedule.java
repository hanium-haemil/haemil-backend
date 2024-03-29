package com.haemil.backend.schedule.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity(name = "schedules")
@Setter
@Getter
@NoArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //일정의 실제 날짜 정보(year, month, day)
    @Column(nullable = false)
    private LocalDate localDate;

    //일정 시간(hour, minute, second, nano)
    @Column(nullable = false)
    private LocalTime time;

    //일정 내용
    @Column(nullable = false, length = 100)
    private String content;

    //일정 완료 여부
    @Column(nullable = false)
    private Boolean done;

    //장소
    @Column(nullable = true, length = 50)
    private String place;

    //반복 routine
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private RepeatType repeatType;

    //장소 url
    @Column(nullable = true)
    private String mapUrl;

}