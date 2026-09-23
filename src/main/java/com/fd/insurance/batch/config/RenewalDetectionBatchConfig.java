package com.fd.insurance.batch.config;

import com.fd.insurance.batch.processor.NotificationProcessor;
import com.fd.insurance.batch.processor.RenewalDetectionBatchProcessor;
import com.fd.insurance.batch.reader.RenewalDetectionReader;
import com.fd.insurance.batch.reader.ReminderOutboxReader;
import com.fd.insurance.batch.writer.NotificationWriter;
import com.fd.insurance.batch.writer.RenewalDetectionWriter;
import com.fd.insurance.entity.Policy;
import com.fd.insurance.entity.ReminderOutbox;
import com.fd.insurance.service.RenewalDetectionService;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class RenewalDetectionBatchConfig {

    @Bean
    public Step renewalDetectionStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            RenewalDetectionReader reader,
            RenewalDetectionBatchProcessor processor,
            RenewalDetectionWriter writer) {

        return new StepBuilder(
                "renewalDetectionStep", jobRepository)
                .<Policy, Policy>chunk(50)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public Step notificationStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ReminderOutboxReader reader,
            NotificationProcessor processor,
            NotificationWriter writer) {

        return new StepBuilder("notificationStep", jobRepository)
                .<ReminderOutbox, ReminderOutbox>chunk(50)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public Job renewalDetectionJob(
            JobRepository jobRepository,
            Step renewalDetectionStep,
            Step notificationStep) {

        return new JobBuilder("renewalDetectionJob", jobRepository)
                .start(renewalDetectionStep)
                .next(notificationStep)
                .build();
    }
}