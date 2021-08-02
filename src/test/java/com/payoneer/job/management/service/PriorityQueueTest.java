package com.payoneer.job.management.service;

import com.payoneer.job.management.enums.JobPriority;
import com.payoneer.job.management.model.Job;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;

@Slf4j
public class PriorityQueueTest {

    private final PriorityQueue<Job> jobPriorityQueue = new PriorityQueue<>();
    @Test
    public void shouldPullItemWithHighPriority(){
        Job job1 = new Job();
        job1.setJobName("Job1");
        job1.setPriority(JobPriority.HIGH);

        Job job2 = new Job();
        job2.setJobName("Job2");
        job2.setPriority(JobPriority.LOW);

        Job job3 = new Job();
        job3.setJobName("Job3");
        job3.setPriority(JobPriority.HIGH);

        jobPriorityQueue.add(job1);
        jobPriorityQueue.add(job2);
        jobPriorityQueue.add(job3);

        log.info("Job ....{}",jobPriorityQueue.poll());
        log.info("Job ....{}",jobPriorityQueue.poll());
        log.info("Job ....{}",jobPriorityQueue.poll());

    }
}
