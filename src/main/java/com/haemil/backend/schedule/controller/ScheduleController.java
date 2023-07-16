package com.haemil.backend.schedule.controller;

import com.haemil.backend.schedule.entity.Schedule;
import com.haemil.backend.schedule.repository.ScheduleRepository;
import com.haemil.backend.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    public final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService){
        this.scheduleService = scheduleService;
    }

    //클라이언트의 요청을 처리하는 컨트롤러 메서드
    @PostMapping //@PostMapping 애너테이션을 통해 HTTP POST 요청을 수신
    public Schedule saveSchedule(@RequestBody Schedule schedule){
        //@RequestBody 애너테이션을 사용하여 요청 본문에 있는 데이터를 Schedule 객체로 변환함
        return scheduleService.saveSchedule(schedule);
        //scheduleService.saveSchedule(schedule)을 호출하여 일정을 저장하고,
        //업데이트된 schedule 객체를 반환
    }





}
