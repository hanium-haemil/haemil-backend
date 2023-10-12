package com.haemil.backend.schedule.dto;

import com.haemil.backend.schedule.entity.RepeatType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@Getter
public class ScheduleRequestDto {

    private Long id;

    private LocalDate localDate;

    private LocalTime time;

    private String content;

    private Boolean done;

    private String place;

    private RepeatType repeatType;


}
