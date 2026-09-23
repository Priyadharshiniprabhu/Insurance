package com.fd.insurance.batch.config;

import com.fd.insurance.batch.processor.PolicyLapseProcessor;
import com.fd.insurance.batch.reader.PolicyLapseReader;
import com.fd.insurance.batch.writer.PolicyLapseWriter;
import com.fd.insurance.entity.Policy;
import com.fd.insurance.service.PolicyLapseService;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class PolicyLapseBatchConfig {

    @Bean
    public Step policyLapseStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            PolicyLapseReader reader,
            PolicyLapseProcessor processor,
            PolicyLapseWriter writer) {

        return new StepBuilder("policyLapseStep", jobRepository)
                .<Policy, Policy>chunk(50)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public Step dailyReportStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            DailyReportTasklet dailyReportTasklet) {

        Tasklet tasklet = (contribution, chunkContext) ->
                dailyReportTasklet.run();

        return new StepBuilder("dailyReportStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    @Bean
    public Job policyLapseJob(
            JobRepository jobRepository,
            Step policyLapseStep,
            Step dailyReportStep) {
        return new JobBuilder("policyLapseJob", jobRepository)
                .start(policyLapseStep)
                .next(dailyReportStep)
                .build();
    }
}
