package com.haemil.backend.schedule.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.bytebuddy.asm.Advice;

import javax.persistence.*;
import java.time.DayOfWeek;
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

    //일정 생성(year, month, day)
    @Column(name = "creationDate", nullable = false)
    private LocalDate creationDate;

    //일정 수정(year, month, day)
    @Column(name = "modificationDate", nullable = false)
    private LocalDate modificationDate;

    //일정의 실제 날짜 정보(year, month, day)
    @Column(nullable = false)
    private LocalDate localDate;

    //요일
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    //일정 시간(hour, minute, second, nano)
    @Column(nullable = false)
    private LocalTime time;

    //일정 내용
    @Column(nullable = true, length = 100)
    private String content;

    //장소
    @Column(nullable = true, length = 50)
    private String place;

    //중요 일정
    @Column(nullable = false)
    private Boolean important_schedule;

    //고정 일정
    @Column(nullable = false)
    private Boolean fixed_schedule;

    public Boolean isImportant(){
        return important_schedule;
    }
    public Boolean isFixed(){
        return fixed_schedule;
    }


}