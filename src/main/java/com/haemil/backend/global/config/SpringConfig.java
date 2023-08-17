package com.haemil.backend.global.config;

import com.haemil.backend.schedule.repository.ScheduleRepository;
import com.haemil.backend.schedule.service.ScheduleService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    private final ScheduleRepository scheduleRepository;

    public SpringConfig(ScheduleRepository scheduleRepository) {this.scheduleRepository = scheduleRepository;}
    @Bean
    public ScheduleService scheduleService(){
        return new ScheduleService(scheduleRepository);
    }


}
