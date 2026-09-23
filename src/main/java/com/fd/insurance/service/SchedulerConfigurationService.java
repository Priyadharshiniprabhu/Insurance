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
    public static final String RENEWAL_DETECTION_JOB = "RenewalDetectionJobSchedular";
    public static final String POLICY_LAPSE_JOB = "PolicyLapseJobSchedular";

    private final SchedulerConfigurationRepository repository;

    public SchedulerConfigurationService(
            SchedulerConfigurationRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void initializeDefaultConfigurations() {
        Map.of(
                DAILY_REPORT_JOB, "0 5 0 * * *",
                RENEWAL_DETECTION_JOB, "0 * * * * *",
                POLICY_LAPSE_JOB, "0 * * * * *"
        ).forEach(this::createIfMissing);
    }

    public String getCronValue(String jobName) {
        return repository.findByName(jobName)
                .map(SchedulerConfiguration::getCronValue)
                .orElseThrow(() -> new IllegalStateException(
                        "No scheduler configuration found for " + jobName));
    }

    private void createIfMissing(String name, String cronValue) {
        Optional<SchedulerConfiguration> existing =
                repository.findByName(name);

        if (existing.isPresent()) {
            SchedulerConfiguration schedulerConfig = existing.get();
            if (schedulerConfig.getCronValue().isBlank()) {
                schedulerConfig.setCronValue(cronValue);
                repository.save(schedulerConfig);
            }
            return;
        }

        repository.save(new SchedulerConfiguration(name, cronValue));
    }
}
