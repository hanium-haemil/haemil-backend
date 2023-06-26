package com.haemil.backend.schedule.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class Schedule {
    @Id
    private Long id;

    //date는 생성과 수정 필요
    private LocalDate date;

    private DayOfWeek week;

    @Column(nullable = true, length = 100)
    private String content;
}