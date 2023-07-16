package com.haemil.backend.schedule.service;

import com.haemil.backend.schedule.entity.Schedule;
import com.haemil.backend.schedule.repository.ScheduleRepository;
//import com.haemil.backend.schedule.repository.SpringDataJpaScheduleRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

//Schedule 객체를 저장하고 조회하는 기능을 구현
//ScheduleRepository를 사용하여 데이터베이스와 상호작용

public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository){
        this.scheduleRepository = scheduleRepository;
    }
    //일정 생성
    public Schedule createSchedule(LocalDate createdDate, DayOfWeek dayOfWeek,
                                   String content, boolean important_schedule,
                                   boolean fixed_schedule, LocalTime time, String place) {
        Schedule schedule = new Schedule();
        schedule.setCreationDate(createdDate);
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setContent(content);
        schedule.setImportant_schedule(important_schedule);
        schedule.setFixed_schedule(fixed_schedule);
        schedule.setTime(time);
        schedule.setPlace(place);

        return scheduleRepository.save(schedule);
    }

    //일정 저장
    public Schedule saveSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    //주어진 날짜에 해당하는 일정 조회
    public List<Schedule> getScheduleByDate(LocalDate localDate, DayOfWeek dayOfWeek) {
        return scheduleRepository.findByLocalDate(localDate);
    }

    //일정 삭제
    @Transactional
    public Long deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
        return scheduleId;
    }

    //오늘에 해당하는 일정 조회
    public List<Schedule> getTodaySchedules(){
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        return scheduleRepository.findByLocalDate(today);
    }

    //일정 수정
    @Transactional
    public Long updateSchedule(Long scheduleId, LocalDate modificationDate,
                                   DayOfWeek dayOfWeek, String content, boolean important_schedule,
                                   boolean fixed_schedule, LocalTime time, String place){

        Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);

        if (((Optional<?>) optionalSchedule).isPresent()){
            Schedule schedule = optionalSchedule.get();
            schedule.setModificationDate(modificationDate);
            schedule.setDayOfWeek(dayOfWeek);
            schedule.setContent(content);
            schedule.setImportant_schedule(important_schedule);
            schedule.setFixed_schedule(fixed_schedule);
            schedule.setTime(time);
            schedule.setPlace(place);
            Schedule updatedSchedule = scheduleRepository.save(schedule);
            return updatedSchedule.getId();

        }else {
            throw new IllegalArgumentException("Invalid schedule ID: " + scheduleId);
        }
    }


}
