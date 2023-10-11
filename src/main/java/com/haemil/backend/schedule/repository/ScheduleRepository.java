package com.haemil.backend.schedule.repository;

import com.haemil.backend.schedule.entity.Schedule;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.swing.text.html.Option;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Primary
public interface ScheduleRepository extends JpaRepository<Schedule,Long> {

    //일정을 조회
    List<Schedule> findByLocalDate(LocalDate localDate);


}
