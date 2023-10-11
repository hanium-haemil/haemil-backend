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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;


@Slf4j
@Transactional
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    //일정 생성
    public ScheduleResponseDto createSchedule(ScheduleRequestDto scheduleRequestDto) throws BaseException {
        try {
            LocalDate localDate = scheduleRequestDto.getLocalDate();
            String content = scheduleRequestDto.getContent();
            Boolean done = scheduleRequestDto.getDone();
            LocalTime time = scheduleRequestDto.getTime();
            String place = scheduleRequestDto.getPlace();
            RepeatType repeatType = scheduleRequestDto.getRepeatType();

            Schedule schedule = new Schedule();

            schedule.setLocalDate(localDate);
            schedule.setContent(content);
            schedule.setDone(done);
            schedule.setTime(time);
            schedule.setPlace(place);
            schedule.setRepeatType(repeatType);

            // missing field 존재 여부 검사
            if (localDate == null || time == null || content == null || done == null || repeatType == null) {
                throw new MissingRequiredFieldException("Required field(s) are missing");
            }

            // 여기서 중복 일정 검사를 수행하고 이미 존재하는 경우 예외를 던짐
            List<Schedule> existingSchedules = scheduleRepository.findByLocalDate(localDate);
            for (Schedule existingSchedule : existingSchedules) {
                if (existingSchedule.getTime().equals(time) &&
                        existingSchedule.getContent().equals(content) &&
                        existingSchedule.getDone().equals(done) &&
                        existingSchedule.getRepeatType().equals(repeatType) &&
                        existingSchedule.getPlace().equals(place)) {
                    throw new BaseException(ResponseStatus.CONFLICT);
                }
            }

            Schedule createdSchedule = scheduleRepository.save(schedule);
            return new ScheduleResponseDto(createdSchedule);
        } catch (MissingRequiredFieldException e) {
            throw new BaseException(ResponseStatus.MISSING_REQUIRED_FIELD);
        } catch (BaseException e) {
            throw new BaseException(ResponseStatus.CONFLICT);
        }


    }

    //주어진 날짜에 해당하는 일정 조회
    public List<Schedule> getSchedule(LocalDate localDate) throws BaseException {
        try {
            List<Schedule> schedules = scheduleRepository.findByLocalDate(localDate);
            if (schedules.isEmpty()) {
                throw new BaseException(ResponseStatus.NOT_FOUND);
            }
            return schedules;
        } catch (BaseException e) {
            throw new BaseException(ResponseStatus.NOT_FOUND);
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
        } catch (BaseException e) {

            throw new BaseException(ResponseStatus.NOT_FOUND);
        }
    }

    //일정 삭제
    @Transactional
    public Long deleteSchedule(Long scheduleId) throws BaseException {
        try {
            Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);

            if (optionalSchedule.isEmpty()) {
                throw new BaseException(ResponseStatus.NOT_FOUND);
            }
            scheduleRepository.deleteById(scheduleId);
            return scheduleId;
        } catch (BaseException e) {

            throw new BaseException(ResponseStatus.NOT_FOUND);
        }
    }

    //일정 수정
    @Transactional
    public Schedule updateSchedule(Long id, ScheduleRequestDto requestDto) {

        try {
            Schedule schedule = scheduleRepository.findById(id).orElse(null);

            LocalDate newLocalDate = requestDto.getLocalDate();
            String newContent = requestDto.getContent();
            Boolean newDone = requestDto.getDone();
            LocalTime newTime = requestDto.getTime();
            String newPlace = requestDto.getPlace();
            RepeatType newRepeatType = requestDto.getRepeatType();

            if (newLocalDate == null || newTime == null || newContent == null || newDone == null || newRepeatType == null) {
                throw new MissingRequiredFieldException("Required field(s) are missing in updated schedule");
            }
            
            // 여기서 중복 일정 검사를 수행하고 이미 존재하는 경우 예외를 던짐
            List<Schedule> existingSchedules = scheduleRepository.findByLocalDate(newLocalDate);
            for (Schedule existingSchedule : existingSchedules) {
                if (
                        existingSchedule.getTime().equals(newTime) &&
                                existingSchedule.getContent().equals(newContent) &&
                                existingSchedule.getDone().equals(newDone) &&
                                existingSchedule.getRepeatType().equals(newRepeatType) &&
                                existingSchedule.getPlace().equals(newPlace)
                ) {
                    throw new BaseException(ResponseStatus.CONFLICT);
                }
            }
            schedule.setLocalDate(newLocalDate);
            schedule.setContent(newContent);
            schedule.setDone(newDone);
            schedule.setTime(newTime);
            schedule.setPlace(newPlace);
            schedule.setRepeatType(newRepeatType);

            return scheduleRepository.save(schedule);

        } catch (MissingRequiredFieldException e) {
            throw new BaseException(ResponseStatus.MISSING_REQUIRED_FIELD);
        } catch (BaseException e) {
            throw new BaseException(ResponseStatus.CONFLICT);
        }
    }
}
