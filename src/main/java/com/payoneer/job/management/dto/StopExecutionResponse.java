package com.payoneer.job.management.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StopExecutionResponse {
    private String message;
}
