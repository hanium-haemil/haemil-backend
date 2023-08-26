package com.haemil.backend.schedule.service;

<<<<<<< HEAD
=======
import com.haemil.backend.global.exception.BaseException;
import com.haemil.backend.global.exception.MissingRequiredFieldException;
import com.haemil.backend.schedule.dto.ScheduleRequestDto;
import com.haemil.backend.schedule.dto.ScheduleResponseDto;
import com.haemil.backend.schedule.entity.RepeatType;
>>>>>>> 3911116 ([FEAT] add schedule api)
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



//Schedule 객체를 저장하고 조회하는 기능을 구현
//ScheduleRepository를 사용하여 데이터베이스와 상호작용
<<<<<<< HEAD

=======
@Slf4j
@Transactional
>>>>>>> 3911116 ([FEAT] add schedule api)
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository){
        this.scheduleRepository = scheduleRepository;
    }
    //일정 생성
<<<<<<< HEAD
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

    //일정 생성
    public ScheduleResponseDto createSchedule(ScheduleRequestDto scheduleRequestDto) throws BaseException {
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

            //log.debug("Schedule fields set....");

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
        } catch (MissingRequiredFieldException e) {
            //log.error("Missing required fields: ", e);
            throw new BaseException(ResponseStatus.MISSING_REQUIRED_FIELD);
        } catch (BaseException e) {
            //log.error("Same field is already exists.: ", e);
            throw new BaseException(ResponseStatus.CONFLICT);
        }


    }

    //주어진 날짜에 해당하는 일정 조회
    public List<Schedule> getSchedule(LocalDate localDate) throws BaseException {
        try {
            //log.debug("1");
            List<Schedule> schedules = scheduleRepository.findByLocalDate(localDate);
            //log.debug("2");
            if (schedules.isEmpty()) {
                throw new BaseException(ResponseStatus.NOT_FOUND);
            }
            return schedules;
        } catch (BaseException e) {
            //log.error("Error occurred while fetching schedules: " + e.getMessage());
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

        } catch (BaseException e) {
            //log.error("Error occurred while fetching schedules: " + e.getMessage());
            throw new BaseException(ResponseStatus.NOT_FOUND); // 현재의 예외를 다시 던져줍니다.
        }
    }

>>>>>>> 3911116 ([FEAT] add schedule api)
    //일정 삭제
    @Transactional
    public Long deleteSchedule(Long scheduleId) {
        try {
            Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);

            if (optionalSchedule.isEmpty()) {
                throw new BaseException(ResponseStatus.NOT_FOUND);
            }
            scheduleRepository.deleteById(scheduleId);
            return scheduleId;
        } catch (BaseException e) {
            //log.error("ID does not exist.");
            throw new BaseException(ResponseStatus.NOT_FOUND);
        }
    }

    //오늘에 해당하는 일정 조회
    public List<Schedule> getTodaySchedules(){
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        return scheduleRepository.findByLocalDate(today);
    }

    //일정 수정
    @Transactional
    public Schedule updateSchedule(Long id, ScheduleRequestDto requestDto) {

        try {
            Schedule schedule = scheduleRepository.findById(id).orElse(null);

            if (schedule == null) {
                throw new BaseException(ResponseStatus.NOT_FOUND); // 해당 아이디가 존재하지 않는 경우
            }
            // Validate the updated schedule's fields
            LocalDate newLocalDate = requestDto.getLocalDate();
            DayOfWeek newDayOfWeek = requestDto.getDayOfWeek();
            String newContent = requestDto.getContent();
            Boolean newDone = requestDto.getDone();
            LocalTime newTime = requestDto.getTime();
            String newPlace = requestDto.getPlace();
            String newMedicine = requestDto.getMedicine();
            RepeatType newRepeatType = requestDto.getRepeatType();

            if (newLocalDate == null || newDayOfWeek == null || newTime == null || newContent == null || newDone == null || newRepeatType == null) {
                throw new MissingRequiredFieldException("Required field(s) are missing in updated schedule");
            }

            schedule.setLocalDate(newLocalDate);
            schedule.setDayOfWeek(newDayOfWeek);
            schedule.setContent(newContent);
            schedule.setDone(newDone);
            schedule.setTime(newTime);
            schedule.setPlace(newPlace);
            schedule.setRepeatType(newRepeatType);
            schedule.setMedicine(newMedicine);

            return scheduleRepository.save(schedule);

        } catch (MissingRequiredFieldException e) {
            //log.error("Missing required fields: ", e);
            throw new BaseException(ResponseStatus.MISSING_REQUIRED_FIELD);
        } catch (BaseException e) {

            throw new BaseException(ResponseStatus.NOT_FOUND);
        }
    }
}
