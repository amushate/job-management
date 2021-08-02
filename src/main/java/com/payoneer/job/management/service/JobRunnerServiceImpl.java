package com.payoneer.job.management.service;

import com.payoneer.job.management.dto.StartExecutionResponse;
import com.payoneer.job.management.dto.StopExecutionResponse;
import com.payoneer.job.management.model.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class JobRunnerServiceImpl implements JobRunnerService {

    private JobSchedulingService schedulingService;

    public JobRunnerServiceImpl(JobSchedulingService schedulingService)  {
        this.schedulingService = schedulingService;
    }

    @Override
    public StartExecutionResponse start() {
        log.info("Starting job management system.....");
        //queue jobs
        schedulingService.scheduleJobs();
        schedulingService.runJobs();
        //execute jobs
        log.info("Job management system stated....");
        return StartExecutionResponse.builder().message("Job execution started").build();
    }

    @Override
    public StopExecutionResponse stop() {
        log.info("Shutting down job management system.....");
        schedulingService.stop();
        return StopExecutionResponse.builder()
                .message("Shutting down ...")
                .build();
    }

    @Override
    public List<Job> listJobs() {
        return schedulingService.getJobs();
    }


}
