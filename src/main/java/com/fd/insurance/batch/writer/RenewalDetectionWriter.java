package com.fd.insurance.batch.writer;

import com.fd.insurance.entity.Policy;
import com.fd.insurance.service.RenewalDetectionService;
import com.fd.insurance.service.RenewalDetectionSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class RenewalDetectionWriter
        implements ItemWriter<Policy>, StepExecutionListener {

    private static final Logger log =
            LoggerFactory.getLogger(RenewalDetectionWriter.class);

    private final RenewalDetectionService renewalDetectionService;
    private final RenewalDetectionSummary jobSummary = new RenewalDetectionSummary();

    public RenewalDetectionWriter(RenewalDetectionService renewalDetectionService) {
        this.renewalDetectionService = renewalDetectionService;
    }

    @Override
    public void write(Chunk<? extends Policy> chunk) {
        jobSummary.add(
                renewalDetectionService.detectRenewalsAndGetSummary(
                        chunk.getItems()));
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        jobSummary.reset();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logSummary();
        return stepExecution.getExitStatus();
    }

    private void logSummary() {
        log.info("=================================");
        log.info("Renewal Detection Summary");
        log.info("Policies Scanned : {}", jobSummary.getScannedPolicies());
        log.info("30 Day Reminders : {}",
                jobSummary.getThirtyDayReminders());
        log.info("15 Day Reminders : {}",
                jobSummary.getFifteenDayReminders());
        log.info("7 Day Reminders : {}",
                jobSummary.getSevenDayReminders());
        log.info("Overdue Reminders : {}",
                jobSummary.getOverdueReminders());
        log.info("Skipped Already Reminded : {}",
                jobSummary.getSkippedAlreadyReminded());
        log.info("=================================");
    }
}
