package com.fd.insurance.batch.config;

import com.fd.insurance.batch.processor.NotificationProcessor;
import com.fd.insurance.batch.reader.ReminderOutboxReader;
import com.fd.insurance.batch.writer.NotificationWriter;
import com.fd.insurance.entity.ReminderOutbox;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;

@Configuration
public class NotificationBatchConfig {

    @Bean
    public Step notificationStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ReminderOutboxReader reader,
            NotificationProcessor processor,
            NotificationWriter writer) {

        return new StepBuilder(
                "notificationStep",
                jobRepository)
                .<ReminderOutbox,
                        ReminderOutbox>
                        chunk(50)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public Job notificationJob(JobRepository jobRepository, Step notificationStep) {

        return new JobBuilder("notificationJob", jobRepository)
                .start(notificationStep)
                .build();
    }
}