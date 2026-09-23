package com.fd.insurance.batch.config;

import com.fd.insurance.batch.processor.RenewalDetectionProcessor;
import com.fd.insurance.batch.reader.PolicyReader;
import com.fd.insurance.batch.writer.ReminderOutboxWriter;
import com.fd.insurance.entity.Policy;
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
            PolicyReader reader,
            RenewalDetectionProcessor processor,
            ReminderOutboxWriter writer) {

        return new StepBuilder(
                "renewalDetectionStep",
                jobRepository)
                .<Policy, Policy>
                        chunk(50)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public Job renewalDetectionJob(JobRepository jobRepository, Step renewalDetectionStep) {

        return new JobBuilder("renewalDetectionJob", jobRepository)
                .start(renewalDetectionStep)
                .build();
    }
}