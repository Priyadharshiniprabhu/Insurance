package com.fd.insurance.service;

import com.fd.insurance.entity.SchedulerConfiguration;
import com.fd.insurance.repository.SchedulerConfigurationRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class SchedulerConfigurationService {

    public static final String DAILY_REPORT_JOB = "DailyReportJobSchedular";
    public static final String NOTIFICATION_JOB = "NotificationJobSchedular";
    public static final String RENEWAL_DETECTION_JOB = "RenewalDetectionJobSchedular";

    private final SchedulerConfigurationRepository repository;

    public SchedulerConfigurationService(
            SchedulerConfigurationRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void initializeDefaultConfigurations() {
        Map.of(
                DAILY_REPORT_JOB, "0 5 0 * * *",
                NOTIFICATION_JOB, "0 * * * * *",
                RENEWAL_DETECTION_JOB, "0 * * * * *"
        ).forEach(this::createIfMissing);
    }

    public String getCronValue(String jobName) {
        return repository.findByName(jobName)
                .map(SchedulerConfiguration::getCronValue)
                .orElseThrow(() -> new IllegalStateException(
                        "No scheduler configuration found for " + jobName));
    }

    private void createIfMissing(String name, String cronValue) {
        SchedulerConfiguration schedulerConfig= repository.findByName(name).get();
        String cronVal=schedulerConfig.getCronValue();
        if (cronVal.isBlank()) {
            schedulerConfig.setCronValue(cronValue);
            repository.save(schedulerConfig);
        }
    }
}
