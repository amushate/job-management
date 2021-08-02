package com.payoneer.job.management.jobs;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoadDWHJob implements Runnable {
    @Override
    public void run() {
        for (int i=0; i<10; i++){
            log.info("Loading data to dwh..."+i);
        }

    }
}