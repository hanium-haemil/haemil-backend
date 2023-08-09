package com.haemil.backend.schedule.dto;

import com.haemil.backend.schedule.entity.RepeatType;
import com.haemil.backend.schedule.entity.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

//schedule에서 값을 가져올 때 직접적인 entity 대신 앞에 써줌
//클라이언트에게 응답할 때 필요한 속성들 추가
@NoArgsConstructor
@Getter
public class ScheduleResponseDto {

    private Long id;

    private LocalDate localDate;

    private DayOfWeek dayOfWeek;

    private LocalTime time;

    private String content;

    private String place;

    private Boolean done;

    private String medicine;

    private RepeatType repeatType;


    public ScheduleResponseDto(Schedule schedule){
        this.id = schedule.getId();

        this.localDate = schedule.getLocalDate();

        this.dayOfWeek = schedule.getDayOfWeek();

        this.time = schedule.getTime();

        this.content = schedule.getContent();

        this.place = schedule.getPlace();

        this.done = schedule.getDone();

        this.medicine = schedule.getMedicine();

        this.repeatType = schedule.getRepeatType();

    }
}
