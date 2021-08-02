package com.payoneer.job.management.controller;

import com.payoneer.job.management.dto.AddJobRequest;
import com.payoneer.job.management.dto.AddJobResponse;
import com.payoneer.job.management.dto.StartExecutionRequest;
import com.payoneer.job.management.dto.StartExecutionResponse;
import com.payoneer.job.management.dto.StopExecutionRequest;
import com.payoneer.job.management.dto.StopExecutionResponse;
import com.payoneer.job.management.model.Job;
import com.payoneer.job.management.service.JobRunnerService;
import com.payoneer.job.management.service.JobSchedulingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/job-management")
public class JobManagementController {

    private JobRunnerService jobRunnerService;
    private JobSchedulingService jobSchedulingService;

    public JobManagementController(JobRunnerService jobRunnerService, JobSchedulingService jobSchedulingService) {
        this.jobRunnerService = jobRunnerService;
        this.jobSchedulingService = jobSchedulingService;
    }
    @PostMapping("/add-job")
    public ResponseEntity<AddJobResponse> addJob(@RequestBody AddJobRequest request) {
        return ResponseEntity.ok(jobSchedulingService.addJob(request));
    }

    @PostMapping("/start-job-execution")
    public ResponseEntity<StartExecutionResponse> startJobExecution(@RequestBody StartExecutionRequest request) {
        //TODO specify whether to run all jobs
        return ResponseEntity.ok(jobRunnerService.start());
    }

    @PostMapping("/stop-job-execution")
    public ResponseEntity<StopExecutionResponse> stopJobExecution(@RequestBody StopExecutionRequest request) {
        //TODO specify whether to stops all jobs
        return ResponseEntity.ok(jobRunnerService.stop());
    }

    @GetMapping("/list-jobs")
    public ResponseEntity<List<Job>> listJobs() {
        return ResponseEntity.ok(jobRunnerService.listJobs());
    }
}
