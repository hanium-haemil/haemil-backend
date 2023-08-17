package com.haemil.backend.schedule.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.haemil.backend.global.config.BaseResponse;
import com.haemil.backend.global.config.ResponseStatus;
import com.haemil.backend.global.exception.BaseException;
import com.haemil.backend.schedule.dto.ScheduleRequestDto;
import com.haemil.backend.schedule.dto.ScheduleResponseDto;
import com.haemil.backend.schedule.entity.Schedule;
import com.haemil.backend.schedule.repository.ScheduleRepository;
import com.haemil.backend.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import static com.haemil.backend.global.config.ResponseStatus.BAD_REQUEST;
import static com.haemil.backend.global.config.ResponseStatus.INTERNAL_SERVER_ERROR;


@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    public final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

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


}
