package com.haemil.backend.schedule.repository;

import com.haemil.backend.schedule.entity.Schedule;
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

//JpaRepository를 상속받아서 데이터베이스와 상호작용하는 메서드들 제공
public interface ScheduleRepository extends JpaRepository<Schedule,Long> {

    //주어진 년, 월, 일에 해당하는 일정을 조회하는 메서드
    List<Schedule> findByLocalDate(LocalDate localDate);


}
