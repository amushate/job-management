package com.payoneer.job.management.service;


import com.payoneer.job.management.dto.AddJobRequest;
import com.payoneer.job.management.dto.AddJobResponse;
import com.payoneer.job.management.dto.StopExecutionResponse;
import com.payoneer.job.management.model.Job;

import java.util.List;

public interface JobSchedulingService {

    public AddJobResponse addJob(AddJobRequest job);

    public Job getJobForExecution();


    boolean hasJobs();

    void scheduleJobs();

    void runJobs();

    StopExecutionResponse stop();

    List<Job> getJobs();
}
