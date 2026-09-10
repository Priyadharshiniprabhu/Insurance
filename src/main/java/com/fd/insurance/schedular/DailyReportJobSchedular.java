package com.fd.insurance.schedular;

import com.fd.insurance.service.DailyReportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DailyReportJobSchedular {

    private final DailyReportService reportService;

    public DailyReportJobSchedular(DailyReportService reportService) {
        this.reportService = reportService;
    }

    @Scheduled(cron = "0 5 0 * * *")
    public void run() {
        reportService.generateReport(LocalDate.now().minusDays(1));
    }
}
