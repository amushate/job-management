package com.payoneer.job.management.model;

import com.payoneer.job.management.enums.JobPriority;
import com.payoneer.job.management.enums.JobStatus;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Job implements Comparable<Job> {

    @Id
    private String jobName;
    private JobStatus status;
    private JobPriority priority;
    private String className;
    private String cronExpression;

    @Override
    public int compareTo(Job job) {
        int value = this.priority.getValue();
        if(value == job.getPriority().getValue()) {
            return 0;
        } else if(value > this.priority.getValue()){
            return 1;
        } else {
            return -1;
        }

    }
}
