package com.payoneer.job.management.repository;

import com.payoneer.job.management.enums.JobStatus;
import com.payoneer.job.management.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {
    List<Job> findByStatus(JobStatus jobStatus);
}
