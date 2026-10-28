package com.fd.insurance;

import com.fd.insurance.service.SchedulerConfigurationService;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class starting implements CommandLineRunner {


    private final JobOperator jobOperator;
    private final JobExplorer jobExplorer;
    private final Job renewalDetectionJob;

    public starting(
            JobOperator jobOperator,
            JobExplorer jobExplorer,
            @Qualifier("renewalDetectionJob") Job renewalDetectionJob,
            SchedulerConfigurationService schedulerConfigurationService) {
        this.jobOperator = jobOperator;
        this.jobExplorer = jobExplorer;
        this.renewalDetectionJob = renewalDetectionJob;
    }

    @Override
    public void run(String... args) throws Exception {

        JobParameters parameters = new JobParametersBuilder()
                .addLong("triggerTime", System.currentTimeMillis())
                .toJobParameters();
        jobOperator.start(renewalDetectionJob, parameters);
    }

}
