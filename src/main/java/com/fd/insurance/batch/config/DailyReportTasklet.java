package com.fd.insurance.batch.config;

import com.fd.insurance.service.DailyReportService;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DailyReportTasklet {

    private final DailyReportService dailyReportService;

    public DailyReportTasklet(DailyReportService dailyReportService) {
        this.dailyReportService = dailyReportService;
    }

    public RepeatStatus run() {
        dailyReportService.generateReport(LocalDate.now());
        return RepeatStatus.FINISHED;
    }
}
