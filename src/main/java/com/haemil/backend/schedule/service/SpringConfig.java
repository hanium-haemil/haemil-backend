package com.haemil.backend.schedule.service;

import com.haemil.backend.schedule.repository.ScheduleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    private final ScheduleRepository scheduleRepository;

    public SpringConfig(ScheduleRepository scheduleRepository){
        this.scheduleRepository = scheduleRepository;
    }

    @Bean
    public ScheduleService scheduleService(){
        return new ScheduleService(scheduleRepository);
    }
}
