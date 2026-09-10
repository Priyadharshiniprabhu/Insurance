package com.fd.insurance.schedular;

import com.fd.insurance.service.RenewalDetectionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RenewalDetectionJobSchedular {

    private final RenewalDetectionService service;

    public RenewalDetectionJobSchedular(RenewalDetectionService service) {
        this.service = service;
        System.out.println("RenewalDetectionJob Bean Created");
    }

    @Scheduled(cron = "0 * * * * *")
    //@Scheduled(cron = "0 0 0 * * *")
    public void run() {

        System.out.println("================================");
        System.out.println("Renewal Detection Job Started");
        System.out.println("Time : " + LocalDateTime.now());

        service.detectRenewals();

        System.out.println("Renewal Detection Job Completed");
        System.out.println("================================");
    }
}
