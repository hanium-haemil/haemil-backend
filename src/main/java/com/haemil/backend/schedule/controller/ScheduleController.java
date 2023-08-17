package com.haemil.backend.schedule.controller;

<<<<<<< HEAD
=======
import com.fasterxml.jackson.databind.ser.Serializers;
import com.haemil.backend.global.config.BaseResponse;
import com.haemil.backend.global.config.ResponseStatus;
import com.haemil.backend.global.exception.BaseException;
import com.haemil.backend.schedule.dto.ScheduleRequestDto;
import com.haemil.backend.schedule.dto.ScheduleResponseDto;
>>>>>>> 3911116 ([FEAT] add schedule api)
import com.haemil.backend.schedule.entity.Schedule;
import com.haemil.backend.schedule.repository.ScheduleRepository;
import com.haemil.backend.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

<<<<<<< HEAD
=======
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.haemil.backend.global.config.ResponseStatus.BAD_REQUEST;
import static com.haemil.backend.global.config.ResponseStatus.INTERNAL_SERVER_ERROR;

>>>>>>> 3911116 ([FEAT] add schedule api)

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    public final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

<<<<<<< HEAD
    //클라이언트의 요청을 처리하는 컨트롤러 메서드
    @PostMapping //@PostMapping 애너테이션을 통해 HTTP POST 요청을 수신
    public Schedule saveSchedule(@RequestBody Schedule schedule){
        //@RequestBody 애너테이션을 사용하여 요청 본문에 있는 데이터를 Schedule 객체로 변환함
        return scheduleService.saveSchedule(schedule);
        //scheduleService.saveSchedule(schedule)을 호출하여 일정을 저장하고,
        //업데이트된 schedule 객체를 반환
    }




=======
    //일정 추가 API
    @PostMapping("/schedule")
    public ResponseEntity<BaseResponse> createSchedule(@RequestBody ScheduleRequestDto scheduleRequestDto) {

        try {
            ScheduleResponseDto createSchedule = scheduleService.createSchedule(scheduleRequestDto);
            BaseResponse<ScheduleResponseDto> response = new BaseResponse<>(createSchedule);
            return response.convert();

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus()).convert();
        }

    }

    //주어진 날짜에 해당하는 일정 조회 API
    @GetMapping("/getSchedule")
    public ResponseEntity<BaseResponse> getSchedulesByDate(@RequestParam("localDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate localDate){
       try {
           List<Schedule> schedules = scheduleService.getSchedule(localDate);
           return new BaseResponse<>(schedules).convert();
       }catch (BaseException e) {
           return new BaseResponse<>(e.getStatus()).convert();
       }
    }


    //오늘 일정 조회 API
    @GetMapping("/today")
    public ResponseEntity<BaseResponse> getTodaySchedules() {
        try {
            List<Schedule> todaySchedules = scheduleService.getTodaySchedules();
            return new BaseResponse<>(todaySchedules).convert();

        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }


    //일정 삭제 API
    @DeleteMapping("/schedule/{scheduleId}")
    public ResponseEntity<BaseResponse> deleteSchedule(@PathVariable Long scheduleId){
        try {
            Long deletedId = scheduleService.deleteSchedule(scheduleId);
            BaseResponse<Long> response = new BaseResponse<>(deletedId);
            return response.convert();
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }


    //일정 수정 API
    @PatchMapping("/schedule/{scheduleId}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable Long scheduleId, @RequestBody ScheduleRequestDto requestDto) {
        Schedule updatedSchedule = scheduleService.updateSchedule(scheduleId, requestDto);

        if (updatedSchedule != null) {
            return ResponseEntity.ok(updatedSchedule);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

>>>>>>> 3911116 ([FEAT] add schedule api)

}
