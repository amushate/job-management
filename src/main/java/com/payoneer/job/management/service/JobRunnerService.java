package com.payoneer.job.management.service;

import com.payoneer.job.management.dto.StartExecutionResponse;
import com.payoneer.job.management.dto.StopExecutionResponse;
import com.payoneer.job.management.model.Job;

import java.util.List;

public interface JobRunnerService {
    public StartExecutionResponse start();

    public StopExecutionResponse stop();

    List<Job> listJobs();
}
