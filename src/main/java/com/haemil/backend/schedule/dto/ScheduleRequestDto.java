package com.haemil.backend.schedule.dto;

import com.haemil.backend.schedule.entity.RepeatType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

//schedule에 데이터를 넣을 때의 입력 요청 값을 받음
@NoArgsConstructor
@Getter
public class ScheduleRequestDto {

    private Long id;

    private LocalDate localDate;

    private DayOfWeek dayOfWeek;

    private LocalTime time;

    private String content;

    private Boolean done;

    private String place;

    private String medicine;

    private RepeatType repeatType;


}
