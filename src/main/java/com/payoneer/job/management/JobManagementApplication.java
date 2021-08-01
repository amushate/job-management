package com.payoneer.job.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class JobManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobManagementApplication.class, args);
	}

}
