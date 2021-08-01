package com.payoneer.job.management.enums;

public enum JobPriority {
    HIGH(1),
    MEDIUM(2),
    LOW(3);

    int value;

    JobPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}