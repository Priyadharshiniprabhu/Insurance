package com.fd.insurance.schedular;

import com.fd.insurance.service.NotificationDispatchService;
import com.fd.insurance.service.DailyReportService;
import com.fd.insurance.service.SchedulerConfigurationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class NotificationJobSchedular {

    private final NotificationDispatchService service;
    private final DailyReportService dailyReportService;

    public NotificationJobSchedular(
            NotificationDispatchService service,
            DailyReportService dailyReportService,
            SchedulerConfigurationService schedulerConfigurationService) {
        this.service = service;
        this.dailyReportService = dailyReportService;
    }

    @Scheduled(cron = "#{@schedulerConfigurationService.getCronValue('NotificationJobSchedular')}")
    public void run() {
        service.processPendingEvents();
        dailyReportService.generateReport(LocalDate.now());
    }
}
