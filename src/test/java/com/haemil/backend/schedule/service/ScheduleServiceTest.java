package com.haemil.backend.schedule.service;

import com.haemil.backend.schedule.entity.Schedule;
import com.haemil.backend.schedule.repository.ScheduleRepository;
import com.haemil.backend.schedule.repository.ScheduleRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class ScheduleServiceTest {

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Test
    void createSchedule(){
        //given
        LocalDate creationDate = LocalDate.now();
        DayOfWeek dayOfWeek = creationDate.getDayOfWeek();
        String content = "Test Schedule";
        boolean important_schedule = true;
        boolean fixed_schedule = false;
        LocalTime time = LocalTime.of(5, 3);
        String place = "Test Place";

        //when
        Schedule schedule = scheduleService.createSchedule(creationDate, dayOfWeek, content, important_schedule, fixed_schedule, time, place);

        //then
        assertThat(schedule.getCreationDate()).isEqualTo(creationDate);
        assertThat(schedule.getDayOfWeek()).isEqualTo(dayOfWeek);
        assertThat(schedule.getContent()).isEqualTo(content);
        assertThat(schedule.isImportant()).isEqualTo(important_schedule);
        assertThat(schedule.isFixed()).isEqualTo(fixed_schedule);
        assertThat(schedule.getTime()).isEqualTo(time);
        assertThat(schedule.getPlace()).isEqualTo(place);

    }


}
