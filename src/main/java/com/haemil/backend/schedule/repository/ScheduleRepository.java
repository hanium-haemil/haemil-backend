package com.haemil.backend.schedule.repository;

import com.haemil.backend.schedule.entity.Schedule;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

@Primary
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    //일정을 조회
    List<Schedule> findByLocalDate(LocalDate localDate);


}