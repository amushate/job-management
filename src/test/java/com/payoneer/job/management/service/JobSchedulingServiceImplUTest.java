package com.payoneer.job.management.service;

import com.payoneer.job.management.dto.AddJobRequest;
import com.payoneer.job.management.enums.JobPriority;
import com.payoneer.job.management.enums.JobStatus;
import com.payoneer.job.management.exceptions.InvalidJobException;
import com.payoneer.job.management.model.Job;
import com.payoneer.job.management.repository.JobRepository;
import org.dozer.Mapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobSchedulingServiceImplUTest {

    @InjectMocks
    private JobSchedulingServiceImpl jobSchedulingService;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ThreadPoolTaskScheduler executor;

    @Mock
    private Mapper mapper;

    @Test(expected = InvalidJobException.class)
    public void shouldThrowIfJobIsNull() {
        AddJobRequest jobRequest = null;
        jobSchedulingService.addJob(jobRequest);
    }

    @Test
    public void shouldAddJobToRepository() {
        AddJobRequest jobRequest = new AddJobRequest();
        Job job = new Job();
        //mocking
        when(mapper.map(jobRequest, Job.class)).thenReturn(job);
        when(jobRepository.save(job)).thenReturn(job);

        //invocation
        jobSchedulingService.addJob(jobRequest);

        //assertions
        verify(jobRepository).save(job);
    }

    @Test
    public void shouldAddJobsToPriorityQueue() {
        jobSchedulingService.scheduleJobs();
        verify(jobRepository).findAll();
    }

    @Test
    public void shouldListAllJobs() {
        jobSchedulingService.getJobs();
        verify(jobRepository).findAll();
    }

    @Test
    public void shouldReturnTrueIfJobsQueueIsEmpty() {
        assertFalse(jobSchedulingService.hasJobs());
    }

    @Test
    public void shouldReturnTrueIfJobsAreQueued() {
        PriorityQueue<Job> priorityQueue = initPriorityQueue();
        priorityQueue.add(new Job());
        assertTrue(jobSchedulingService.hasJobs());
    }

    @Test
    public void shouldReturnJobWithHighPriority() {
        Job highPriorityJob = new Job();
        highPriorityJob.setPriority(JobPriority.HIGH);

        Job lowPriorityJob = new Job();
        lowPriorityJob.setPriority(JobPriority.LOW);

        PriorityQueue<Job> priorityQueue = initPriorityQueue();
        priorityQueue.add(lowPriorityJob);
        priorityQueue.add(highPriorityJob);

        ReflectionTestUtils.setField(jobSchedulingService, "jobPriorityQueue", priorityQueue);
        Job job = jobSchedulingService.getJobForExecution();
        assertEquals(JobPriority.HIGH, job.getPriority());
    }

    @Test
    public void shouldReturnNullIfQueueIsEmpty() {
        PriorityQueue<Job> priorityQueue = new PriorityQueue<>();
        ReflectionTestUtils.setField(jobSchedulingService, "jobPriorityQueue", priorityQueue);
        Job job = jobSchedulingService.getJobForExecution();
        assertNull(job);
    }

    @Test(expected = InvalidJobException.class)
    public void shouldFailIfJobClassIsEmpty() throws ExecutionException, ClassNotFoundException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        jobSchedulingService.executeJob(new Job());
    }

    @Test
    public void shouldExecuteNonCronJob() throws ExecutionException, ClassNotFoundException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Job job = new Job();
        job.setClassName("com.payoneer.job.management.jobs.FileIndexingJob");
        Future<?> future = mock(Future.class);
        doReturn(future).when(executor).submit(any(Runnable.class));
        String result = "";
        doReturn(result).when(future).get();
        jobSchedulingService.executeJob(job);
    }

    @Test
    public void shouldRunJob() throws ExecutionException, InterruptedException {
        PriorityQueue<Job> priorityQueue = initPriorityQueue();
        priorityQueue.add(nonCronJob());

        Future<?> future = mock(Future.class);
        doReturn(future).when(executor).submit(any(Runnable.class));
        String result = "";
        doReturn(result).when(future).get();

        jobSchedulingService.runJobs();
    }

    @Test
    public void shouldMarkFailedIfJobFails(){
        PriorityQueue<Job> priorityQueue = initPriorityQueue();
        Job job = nonCronJob();
        job.setClassName("");
        priorityQueue.add(job);
        jobSchedulingService.runJobs();
    }

    @Test
    public void shouldExecuteCronJob() throws ExecutionException, ClassNotFoundException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Job queuedJob = cronJob();

        Job successJob = cronJob();
        successJob.setStatus(JobStatus.SUCCESS);
        when(jobRepository.save(queuedJob)).thenReturn(successJob);

        ScheduledFuture<?> future = mock(ScheduledFuture.class);
        doReturn(future).when(executor).schedule(any(Runnable.class), any(CronTrigger.class));
        String result = "";
        doReturn(result).when(future).get();

        jobSchedulingService.executeJob(queuedJob);
    }

    @Test
    public void shouldShutDown() {
        ScheduledThreadPoolExecutor scheduledExecutor = mock(ScheduledThreadPoolExecutor.class);
        when(executor.getScheduledThreadPoolExecutor()).thenReturn(scheduledExecutor);
        List<Job> runningJobs = new ArrayList<>();
        runningJobs.add(nonCronRunningJob());
        when(jobRepository.findByStatus(JobStatus.RUNNING)).thenReturn(runningJobs);
        jobSchedulingService.stop();
        verify(executor.getScheduledThreadPoolExecutor()).shutdownNow();
    }

    private Job cronJob(){
        Job job = new Job();
        job.setClassName("com.payoneer.job.management.jobs.FileIndexingJob");
        job.setCronExpression("1 * * * * *");
        return job;
    }

    private Job nonCronJob(){
        Job job = new Job();
        job.setClassName("com.payoneer.job.management.jobs.FileIndexingJob");
        return job;
    }

    private Job nonCronRunningJob(){
        Job job = new Job();
        job.setStatus(JobStatus.RUNNING);
        job.setClassName("com.payoneer.job.management.jobs.FileIndexingJob");
        return job;
    }


    private PriorityQueue<Job> initPriorityQueue(){
        PriorityQueue<Job> priorityQueue = new PriorityQueue<>();
        ReflectionTestUtils.setField(jobSchedulingService, "jobPriorityQueue", priorityQueue);
        return priorityQueue;
    }
}
