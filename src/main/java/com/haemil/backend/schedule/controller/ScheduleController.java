package com.haemil.backend.schedule.controller;

import com.haemil.backend.global.config.BaseResponse;
import com.haemil.backend.global.exception.BaseException;
import com.haemil.backend.schedule.service.MapService;
import com.haemil.backend.schedule.dto.ScheduleRequestDto;
import com.haemil.backend.schedule.dto.ScheduleResponseDto;
import com.haemil.backend.schedule.entity.Schedule;
import com.haemil.backend.schedule.service.ScheduleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/schedules")
@AllArgsConstructor
@Slf4j
public class ScheduleController {

    public final ScheduleService scheduleService;
    public final MapService mapService;

    //일정 추가 API
    @PostMapping("/schedule")
    public ResponseEntity<BaseResponse> createSchedule(
        @RequestBody ScheduleRequestDto scheduleRequestDto) {
        try {
            //schedule 생성 및 map URL 설정
            ScheduleResponseDto createSchedule = scheduleService.createSchedule(scheduleRequestDto);
            String mapUrlString = mapService.getMapUrl(scheduleRequestDto.getPlace());
            createSchedule.setMapUrl(mapUrlString);
            BaseResponse<ScheduleResponseDto> response = new BaseResponse<>(createSchedule);
            return response.convert();
        } catch (BaseException e) {
            //에러 처리
            return new BaseResponse<>(e.getStatus()).convert();
        } catch (com.haemil.backend.global.config.BaseException e) {
            //에러 처리
            throw new RuntimeException(e);
        }
    }

    //주어진 날짜에 해당하는 일정 조회 API
    @GetMapping("/getSchedule")
    public ResponseEntity<BaseResponse> getSchedulesByDate(
        @RequestParam("localDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate localDate) {
        try {
            //원하는 schedule 조회
            List<Schedule> schedules = scheduleService.getSchedule(localDate);
            return new BaseResponse<>(schedules).convert();
        } catch (BaseException e) {
            //에러 처리
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }

    //오늘 일정 조회 API
    @GetMapping("/today")
    public ResponseEntity<BaseResponse> getTodaySchedules() {
        try {
            //오늘 schedule 조회
            List<Schedule> todaySchedules = scheduleService.getTodaySchedules();
            return new BaseResponse<>(todaySchedules).convert();
        } catch (BaseException e) {
            //에러 처리
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }

    //일정 삭제 API
    @DeleteMapping("/schedule/{scheduleId}")
    public ResponseEntity<BaseResponse> deleteSchedule(@PathVariable Long scheduleId) {
        try {
            //schedule 삭제
            Long deletedId = scheduleService.deleteSchedule(scheduleId);
            BaseResponse<Long> response = new BaseResponse<>(deletedId);
            return response.convert();
        } catch (BaseException e) {
            //에러 처리
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }

    //일정 수정 API
    @PatchMapping("/schedule/{scheduleId}")
    public ResponseEntity<BaseResponse> updateSchedule(@PathVariable Long scheduleId,
        @RequestBody ScheduleRequestDto requestDto) {
        try {
            //schedule 수정 및 map URL 수정
            Schedule updateSchedule = scheduleService.updateSchedule(scheduleId, requestDto);
            String mapUrlString = mapService.getMapUrl(requestDto.getPlace());
            updateSchedule.setMapUrl(mapUrlString);

            ScheduleResponseDto responseDto = new ScheduleResponseDto(updateSchedule);
            BaseResponse<ScheduleResponseDto> response = new BaseResponse<>(responseDto);
            return response.convert();
        } catch (BaseException e) {
            //에러 처리
            return new BaseResponse<>(e.getStatus()).convert();
        } catch (com.haemil.backend.global.config.BaseException e) {
            //에러 처리
            throw new RuntimeException(e);
        }
    }
}
