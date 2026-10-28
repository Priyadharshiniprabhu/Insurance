package com.fd.insurance.dto;

import java.time.ZonedDateTime;

public class SchedulerStatusResponse {

    private final String name;
    private final String cronValue;
    private final ZonedDateTime nextRun;

    public SchedulerStatusResponse(
            String name,
            String cronValue,
            ZonedDateTime nextRun) {
        this.name = name;
        this.cronValue = cronValue;
        this.nextRun = nextRun;
    }

    public String getName() {
        return name;
    }

    public String getCronValue() {
        return cronValue;
    }

    public ZonedDateTime getNextRun() {
        return nextRun;
    }
}
