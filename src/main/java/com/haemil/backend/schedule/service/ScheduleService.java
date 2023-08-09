package com.haemil.backend.schedule.service;

import com.haemil.backend.schedule.dto.ScheduleRequestDto;
import com.haemil.backend.schedule.dto.ScheduleResponseDto;
import com.haemil.backend.schedule.entity.RepeatType;
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

@Transactional
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    //일정 생성
    public ScheduleResponseDto createSchedule(ScheduleRequestDto scheduleRequestDto) {

        LocalDate localDate = scheduleRequestDto.getLocalDate();
        DayOfWeek dayOfWeek = scheduleRequestDto.getDayOfWeek();
        String content = scheduleRequestDto.getContent();
        Boolean done = scheduleRequestDto.getDone();
        LocalTime time = scheduleRequestDto.getTime();
        String place = scheduleRequestDto.getPlace();
        String medicine = scheduleRequestDto.getMedicine();
        RepeatType repeatType = scheduleRequestDto.getRepeatType();

        Schedule schedule = new Schedule();

        schedule.setLocalDate(localDate);
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setContent(content);
        schedule.setDone(done);
        schedule.setTime(time);
        schedule.setPlace(place);
        schedule.setRepeatType(repeatType);
        schedule.setMedicine(medicine);

        Schedule createdSchedule = scheduleRepository.save(schedule);

        return new ScheduleResponseDto(createdSchedule);
    }

    //주어진 날짜에 해당하는 일정 조회
    public List<Schedule> getSchedule(LocalDate localDate) {
        return scheduleRepository.findByLocalDate(localDate);
    }

    //오늘에 해당하는 일정 조회
    public List<Schedule> getTodaySchedules() {
        LocalDate today = LocalDate.now();
        //DayOfWeek dayOfWeek = today.getDayOfWeek();
        return scheduleRepository.findByLocalDate(today);
    }

    //일정 삭제
    @Transactional
    public Long deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
        return scheduleId;
    }

    //일정 수정
    @Transactional
    public Schedule updateSchedule(Long id, ScheduleRequestDto requestDto) {
        Schedule schedule = scheduleRepository.findById(id).orElse(null);

        if (schedule == null) {
            throw new IllegalArgumentException("해당 아이디가 존재하지 않습니다");
        }

        schedule.setLocalDate(requestDto.getLocalDate());
        schedule.setDayOfWeek(requestDto.getDayOfWeek());
        schedule.setContent(requestDto.getContent());
        schedule.setDone(requestDto.getDone());
        schedule.setTime(requestDto.getTime());
        schedule.setPlace(requestDto.getPlace());
        schedule.setRepeatType(requestDto.getRepeatType());
        schedule.setMedicine(requestDto.getMedicine());

        return scheduleRepository.save(schedule);
    }


}
