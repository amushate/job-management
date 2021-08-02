package com.payoneer.job.management.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class JobRunnerServiceImplUTest {
    @InjectMocks
    private JobRunnerServiceImpl jobRunnerService;

    @Mock
    private JobSchedulingServiceImpl jobSchedulingService;

    @Test
    public void shouldStartRunningJobs() {
        jobRunnerService.start();
        verify(jobSchedulingService).runJobs();
    }

    @Test
    public void shouldStopRunningJobs() {
        jobRunnerService.stop();
        verify(jobSchedulingService).stop();
    }

    @Test
    public void shouldListJobs() {
        jobRunnerService.listJobs();
        verify(jobSchedulingService).getJobs();
    }
}
