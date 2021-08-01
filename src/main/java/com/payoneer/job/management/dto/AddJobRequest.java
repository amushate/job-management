package com.payoneer.job.management.dto;

import com.payoneer.job.management.enums.JobPriority;
import lombok.Data;

@Data
public class AddJobRequest {
    private String jobName;
    private JobPriority priority;
    private String cronExpression;
    private String className;
}
