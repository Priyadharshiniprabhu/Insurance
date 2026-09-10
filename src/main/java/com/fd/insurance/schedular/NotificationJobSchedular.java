package com.fd.insurance.schedular;

import com.fd.insurance.service.NotificationDispatchService;
import com.fd.insurance.service.DailyReportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class NotificationJobSchedular {

    private final NotificationDispatchService service;
    private final DailyReportService dailyReportService;

    public NotificationJobSchedular(
            NotificationDispatchService service,
            DailyReportService dailyReportService) {
        this.service = service;
        this.dailyReportService = dailyReportService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void run() {
        service.processPendingEvents();
        dailyReportService.generateReport(LocalDate.now());
    }
}
