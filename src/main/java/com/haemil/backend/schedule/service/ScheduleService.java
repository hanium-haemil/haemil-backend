package com.haemil.backend.schedule.service;

import com.haemil.backend.global.exception.BaseException;
import com.haemil.backend.global.exception.MissingRequiredFieldException;
import com.haemil.backend.schedule.dto.ScheduleRequestDto;
import com.haemil.backend.schedule.dto.ScheduleResponseDto;
import com.haemil.backend.schedule.entity.RepeatType;
import com.haemil.backend.schedule.entity.Schedule;
import com.haemil.backend.schedule.repository.ScheduleRepository;
//import com.haemil.backend.schedule.repository.SpringDataJpaScheduleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.haemil.backend.global.config.ResponseStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;



//Schedule 객체를 저장하고 조회하는 기능을 구현
//ScheduleRepository를 사용하여 데이터베이스와 상호작용
@Slf4j
@Transactional
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    //일정 생성
    public ScheduleResponseDto createSchedule(ScheduleRequestDto scheduleRequestDto) throws BaseException{

        //String responseBody;

        try {
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

            log.debug("Schedule fields set....");

            // missing field 존재 여부 검사
            if (localDate == null || dayOfWeek == null || time == null || content == null || done == null || repeatType == null) {
                throw new MissingRequiredFieldException("Required field(s) are missing");
            }

            // 여기서 중복 일정 검사를 수행하고 이미 존재하는 경우 예외를 던짐
            List<Schedule> existingSchedules = scheduleRepository.findByLocalDate(localDate);
            for (Schedule existingSchedule : existingSchedules) {
                if (existingSchedule.getDayOfWeek() == dayOfWeek &&
                        existingSchedule.getTime().equals(time) &&
                        existingSchedule.getContent().equals(content) &&
                        existingSchedule.getDone().equals(done) &&
                        existingSchedule.getRepeatType().equals(repeatType) &&
                        existingSchedule.getPlace().equals(place) &&
                        existingSchedule.getMedicine().equals(medicine)) {
                    throw new BaseException(ResponseStatus.CONFLICT);
                }
            }

            Schedule createdSchedule = scheduleRepository.save(schedule);
            return new ScheduleResponseDto(createdSchedule);


        } catch(MissingRequiredFieldException e) {
            log.error("Missing required fields: ", e);
            throw new BaseException(ResponseStatus.MISSING_REQUIRED_FIELD);
        } catch (BaseException e) {
            log.error("Same field is already exists.: ", e);
            throw new BaseException(ResponseStatus.CONFLICT);
        }


    }

    //주어진 날짜에 해당하는 일정 조회
    public List<Schedule> getSchedule(LocalDate localDate) throws BaseException{
        try {
            List<Schedule> schedules = scheduleRepository.findByLocalDate(localDate);

            log.debug("1");
        if (schedules.isEmpty()) {
                  throw new BaseException(ResponseStatus.NOT_FOUND);
              }

            log.debug("2");

            return schedules;
        }catch(BaseException e){
            log.error("Error occurred while fetching schedules: " + e.getMessage());
            throw new BaseException(ResponseStatus.NOT_FOUND); // 현재의 예외를 다시 던져줍니다.
        }

    }


    //오늘에 해당하는 일정 조회
    public List<Schedule> getTodaySchedules() throws BaseException {
        try {
            LocalDate today = LocalDate.now();
            List<Schedule> todaySchedules = scheduleRepository.findByLocalDate(today);

            if (todaySchedules.isEmpty()) {
                throw new BaseException(ResponseStatus.NOT_FOUND);
            }

            return todaySchedules;

        } catch(BaseException e){
            log.error("Error occurred while fetching schedules: " + e.getMessage());
            throw new BaseException(ResponseStatus.NOT_FOUND); // 현재의 예외를 다시 던져줍니다.
        }
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
