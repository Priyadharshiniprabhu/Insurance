package com.fd.insurance.schedular;

import com.fd.insurance.service.DailyReportService;
import com.fd.insurance.service.SchedulerConfigurationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DailyReportJobSchedular {

    private final DailyReportService reportService;

    public DailyReportJobSchedular(
            DailyReportService reportService,
            SchedulerConfigurationService schedulerConfigurationService) {
        this.reportService = reportService;
    }

    @Scheduled(cron = "#{@schedulerConfigurationService.getCronValue('DailyReportJobSchedular')}")
    public void run() {
        reportService.generateReport(LocalDate.now().minusDays(1));
    }
}
