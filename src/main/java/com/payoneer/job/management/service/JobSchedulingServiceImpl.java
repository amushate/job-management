package com.payoneer.job.management.service;

import com.payoneer.job.management.dto.AddJobRequest;
import com.payoneer.job.management.dto.AddJobResponse;
import com.payoneer.job.management.dto.StopExecutionResponse;
import com.payoneer.job.management.enums.JobStatus;
import com.payoneer.job.management.exceptions.InvalidJobException;
import com.payoneer.job.management.model.Job;
import com.payoneer.job.management.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.dozer.Mapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Service
public class JobSchedulingServiceImpl implements JobSchedulingService {

    private JobRepository jobRepository;
    private Mapper mapper;
    private ThreadPoolTaskScheduler executor;
    private PriorityQueue<Job> jobPriorityQueue; // create a jobs heap, highest priority job will be on top

    public JobSchedulingServiceImpl(JobRepository jobRepository, Mapper mapper, ThreadPoolTaskScheduler executor) {
        this.jobRepository = jobRepository;
        this.mapper = mapper;
        this.executor = executor;
        jobPriorityQueue = new PriorityQueue<>();
    }

    @Override
    public AddJobResponse addJob(AddJobRequest addJobRequest) {
        //TODO add extra validations to check job properties are not empty
        if (addJobRequest == null) {
            log.error("Cant add a null job");
            throw new InvalidJobException("Cant add a null job");
        }
        Job job = mapper.map(addJobRequest, Job.class);
        job = jobRepository.save(job);
        log.info("New Job added {}", job);
        return AddJobResponse.builder().message("Job added successfully").build();
    }

    @Override
    public Job getJobForExecution() {
        Job job = jobPriorityQueue.poll();
        if (job != null) {
            return job;
        }
        return null;
    }

    @Override
    public boolean hasJobs(){
        return !jobPriorityQueue.isEmpty();
    }

    @Override
    public void scheduleJobs() {
        jobPriorityQueue = new PriorityQueue<>();
        List<Job> jobs = jobRepository.findAll();
        jobPriorityQueue.addAll(jobs);
    }

    @Async
    @Override
    public void runJobs() {
        executor.setWaitForTasksToCompleteOnShutdown(false);
        executor.initialize();
        while (hasJobs()) {
            Job job = getJobForExecution();
            try {
                executeJob(job);
            } catch (Exception e) {
                log.error("Failed to execute job", e);
                job.setStatus(JobStatus.FAILED);
                jobRepository.save(job);
            }
        }
    }

    public void executeJob(Job job) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ExecutionException, InterruptedException {
        if (job != null && job.getClassName() != null) {
            Class<?> aClass = Class.forName(job.getClassName());
            Runnable runnable = (Runnable) aClass.getDeclaredConstructor().newInstance();
            if (job.getCronExpression() != null) {
                //TODO cro scheduled jobs should set status to running on next execution time.
                Future<?> future = executor.schedule(runnable, new CronTrigger(job.getCronExpression()));
                job.setStatus(JobStatus.RUNNING);
                jobRepository.save(job);
                future.get();
            } else {
                Future<?> future = executor.submit(runnable);
                job.setStatus(JobStatus.RUNNING);
                jobRepository.save(job);
                future.get();
            }
            job.setStatus(JobStatus.SUCCESS);
            job = jobRepository.save(job);
            log.info("Job executed successfully {}", job);
        } else {
            log.error("Failed to execute job {}", job);
            throw new InvalidJobException("Job not properly configured");
        }
    }

    @Override
    public StopExecutionResponse stop() {
        executor.getScheduledThreadPoolExecutor().shutdownNow();
        jobRepository.findByStatus(JobStatus.RUNNING).forEach(job -> {
            job.setStatus(JobStatus.QUEUED);
            jobRepository.save(job);
        });
        log.info("Shutting down completed.....");
        return StopExecutionResponse.builder()
                .message("Job Execution stopped")
                .build();
    }

    @Override
    public List<Job> getJobs() {
        return jobRepository.findAll();
    }

}
