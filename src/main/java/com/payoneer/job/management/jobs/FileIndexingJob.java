package com.payoneer.job.management.jobs;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class FileIndexingJob implements Runnable {

    @SneakyThrows
    @Override
    public void run() {
        for(int i=0; i<10; i++){
            log.info("Index File Job.....{}", i);
            Thread.sleep(1000);
        }
        log.info("Completed File indexing..."); ;
    }
}
