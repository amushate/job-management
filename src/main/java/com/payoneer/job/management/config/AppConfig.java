package com.payoneer.job.management.config;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class AppConfig {

    @Bean
    public Mapper mapper(){
        return new DozerBeanMapper();
    }

    @Bean
    public ThreadPoolTaskScheduler executor() {
        return new ThreadPoolTaskScheduler();
    }


}
