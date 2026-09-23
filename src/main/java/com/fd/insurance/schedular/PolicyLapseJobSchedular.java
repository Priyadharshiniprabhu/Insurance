package com.fd.insurance.schedular;

import com.fd.insurance.service.SchedulerConfigurationService;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.explore.JobExplorer;
import org.springframework.batch.core.BatchStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PolicyLapseJobSchedular {

    private final JobOperator jobOperator;
    private final JobExplorer jobExplorer;
    private final Job policyLapseJob;

    public PolicyLapseJobSchedular(
            JobOperator jobOperator,
            JobExplorer jobExplorer,
            @Qualifier("policyLapseJob") Job policyLapseJob,
            SchedulerConfigurationService schedulerConfigurationService) {
        this.jobOperator = jobOperator;
        this.jobExplorer = jobExplorer;
        this.policyLapseJob = policyLapseJob;
        System.out.println("PolicyLapseJob Bean Created");
    }

    @Scheduled(cron = "#{@schedulerConfigurationService.getCronValue('PolicyLapseJobSchedular')}")
    public void run()
            throws JobInstanceAlreadyCompleteException,
            InvalidJobParametersException,
            JobExecutionAlreadyRunningException,
            JobRestartException,
            NoSuchJobExecutionException,
            NoSuchJobException {

        System.out.println("================================");
        System.out.println("Policy Lapse Job Started");
        System.out.println("Time : " + LocalDateTime.now());

        launchOrRestart();

        System.out.println("Policy Lapse Job Completed");
        System.out.println("================================");
    }

    private void launchOrRestart()
            throws JobInstanceAlreadyCompleteException,
            InvalidJobParametersException,
            JobExecutionAlreadyRunningException,
            JobRestartException,
            NoSuchJobExecutionException,
            NoSuchJobException {

        var lastInstance = jobExplorer.getLastJobInstance("policyLapseJob");
        var lastExecution = lastInstance == null
                ? null
                : jobExplorer.getLastJobExecution(lastInstance);

        if (lastExecution != null
                && (lastExecution.getStatus() == BatchStatus.FAILED
                || lastExecution.getStatus() == BatchStatus.STOPPED)) {
            jobOperator.restart(lastExecution.getId());
            return;
        }

        JobParameters parameters = new JobParametersBuilder()
                .addLong("triggerTime", System.currentTimeMillis())
                .toJobParameters();
        jobOperator.start(policyLapseJob, parameters);
    }
}
