package com.fd.insurance.schedular;

import com.fd.insurance.service.RenewalDetectionService;
import com.fd.insurance.service.SchedulerConfigurationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RenewalDetectionJobSchedular {

    private final RenewalDetectionService service;

    public RenewalDetectionJobSchedular(
            RenewalDetectionService service,
            SchedulerConfigurationService schedulerConfigurationService) {
        this.service = service;
        System.out.println("RenewalDetectionJob Bean Created");
    }

    @Scheduled(cron = "#{@schedulerConfigurationService.getCronValue('RenewalDetectionJobSchedular')}")
    public void run() {

        System.out.println("================================");
        System.out.println("Renewal Detection Job Started");
        System.out.println("Time : " + LocalDateTime.now());

        service.detectRenewals();

        System.out.println("Renewal Detection Job Completed");
        System.out.println("================================");
    }
}
