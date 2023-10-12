package com.haemil.backend.schedule.dto;

import com.haemil.backend.schedule.entity.RepeatType;
import com.haemil.backend.schedule.entity.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@Getter
@Setter
public class ScheduleResponseDto {

    private Long id;

    private LocalDate localDate;

    private LocalTime time;

    private String content;

    private Boolean done;

    private String place;

    private RepeatType repeatType;

    private String mapUrl;


    public ScheduleResponseDto(Schedule schedule) {

        this.id = schedule.getId();

        this.localDate = schedule.getLocalDate();

        this.time = schedule.getTime();

        this.content = schedule.getContent();

        this.done = schedule.getDone();

        this.place = schedule.getPlace();

        this.repeatType = schedule.getRepeatType();

        this.mapUrl = schedule.getMapUrl();

    }
}
