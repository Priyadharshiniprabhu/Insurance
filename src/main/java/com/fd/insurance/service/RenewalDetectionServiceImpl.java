package com.fd.insurance.service;

import com.fd.insurance.entity.Policy;
import com.fd.insurance.entity.ReminderOutbox;
import com.fd.insurance.enums.ChannelType;
import com.fd.insurance.enums.PolicyStatus;
import com.fd.insurance.enums.ReminderTier;
import com.fd.insurance.repository.PolicyRepository;
import com.fd.insurance.repository.ReminderOutboxRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RenewalDetectionServiceImpl
        implements RenewalDetectionService {

    private static final Logger JOB_LOGGER = LoggerFactory.getLogger("RENEWAL_JOB");
    private static final DateTimeFormatter LOG_ARCHIVE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS");

    private final PolicyRepository policyRepository;
    private final ReminderOutboxRepository outboxRepository;

    public RenewalDetectionServiceImpl(
            PolicyRepository policyRepository,
            ReminderOutboxRepository outboxRepository) {

        this.policyRepository = policyRepository;
        this.outboxRepository = outboxRepository;
    }

    @Override
    @Transactional
    public void detectRenewals() {

        int scannedPolicies = 0;

        int thirtyDayCount = 0;
        int fifteenDayCount = 0;
        int sevenDayCount = 0;
        int overdueCount = 0;

        int skippedCount = 0;

        List<Policy> policies = policyRepository.findPoliciesForReminderDetection();
        System.out.println("Policies Found = " + policies.size());

        LocalDate today = LocalDate.now();

        for (Policy policy : policies) {

            scannedPolicies++;

            ReminderTier tier = null;

            long days = ChronoUnit.DAYS.between(today, policy.getEndDate());

            if (policy.getStatus() == PolicyStatus.ACTIVE) {

                if (days == 30) {

                    tier = ReminderTier.THIRTY_DAY;

                } else if (days == 15) {

                    tier = ReminderTier.FIFTEEN_DAY;

                } else if (days == 7) {

                    tier = ReminderTier.SEVEN_DAY;
                }
            }

            if (policy.getStatus() == PolicyStatus.EXPIRED) {

                LocalDate graceEndDate =
                        policy.getEndDate()
                                .plusDays(15);

                if (today.isAfter(graceEndDate)) {

                    policyRepository.updatePolicyStatus(
                            policy.getPolicyNumber(),
                            PolicyStatus.LAPSED);

                    JOB_LOGGER.info(
                            "Policy {} marked as LAPSED",
                            policy.getPolicyNumber());

                    continue;
                }
            }

            if (policy.getStatus() == PolicyStatus.EXPIRED) {

                System.out.println("Expired Policy : " + policy.getPolicyNumber());

                LocalDate graceEndDate = policy.getEndDate().plusDays(15);

                if (!today.isAfter(graceEndDate)) {

                    tier = ReminderTier.OVERDUE;

                    System.out.println("OVERDUE tier applied for " + policy.getPolicyNumber());
                }
            }

            if (tier == null) {
                continue;
            }

            if (policy.getStatus() == PolicyStatus.ACTIVE) {

                policyRepository.updatePolicyStatus(policy.getPolicyNumber(), PolicyStatus.RENEWAL_DUE);
            }

            boolean emailCreated = createOutboxRecord(policy, tier, ChannelType.EMAIL);
            boolean smsCreated = createOutboxRecord(policy, tier, ChannelType.SMS);

            if (emailCreated || smsCreated) {

                switch (tier) {

                    case THIRTY_DAY ->
                            thirtyDayCount++;

                    case FIFTEEN_DAY ->
                            fifteenDayCount++;

                    case SEVEN_DAY ->
                            sevenDayCount++;

                    case OVERDUE ->
                            overdueCount++;
                }

            } else {

                skippedCount++;
            }
        }

        writeSummaryLog(scannedPolicies, thirtyDayCount, fifteenDayCount,
                sevenDayCount, overdueCount, skippedCount);
    }

    private void writeSummaryLog(
            int scannedPolicies,
            int thirtyDayCount,
            int fifteenDayCount,
            int sevenDayCount,
            int overdueCount,
            int skippedCount) {
        Path logDirectory = Path.of("logs");
        Path activeLog = logDirectory.resolve("renewal-detection.log");
        String summary = String.format("""
                %s INFO  - =================================
                %s INFO  - Renewal Detection Summary
                %s INFO  - Policies Scanned : %d
                %s INFO  - 30 Day Reminders : %d
                %s INFO  - 15 Day Reminders : %d
                %s INFO  - 7 Day Reminders : %d
                %s INFO  - Overdue Reminders : %d
                %s INFO  - Skipped Already Reminded : %d
                %s INFO  - =================================
                """,
                logTimestamp(), logTimestamp(), logTimestamp(), scannedPolicies,
                logTimestamp(), thirtyDayCount, logTimestamp(), fifteenDayCount,
                logTimestamp(), sevenDayCount, logTimestamp(), overdueCount,
                logTimestamp(), skippedCount, logTimestamp());

        try {
            Files.createDirectories(logDirectory);
            if (Files.exists(activeLog) && Files.size(activeLog) > 0) {
                Path archive = logDirectory.resolve(
                        "renewal-detection-" +
                                LocalDateTime.now().format(LOG_ARCHIVE_FORMAT) +
                                ".log");
                Files.move(activeLog, archive);
            }
            Files.writeString(activeLog, summary);
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Unable to write renewal detection summary", ex);
        }
    }

    private String logTimestamp() {
        return LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    private boolean createOutboxRecord(Policy policy, ReminderTier tier, ChannelType channel) {

        if(outboxRepository.exists(policy.getPolicyNumber(), tier, channel)) {
            return false;
        }

        ReminderOutbox outbox = new ReminderOutbox();

        outbox.setPolicyNumber(policy.getPolicyNumber());
        outbox.setTier(tier);
        outbox.setChannel(channel);
        outbox.setPayload(
                "Policy Number : "
                        + policy.getPolicyNumber()
                        + ", Holder Name : "
                        + policy.getHolderName()
                        + ", Expiry Date : "
                        + policy.getEndDate());

        outbox.setStatus("PENDING");
        outbox.setRetryCount(0);
        outbox.setNextRetryTimestamp(LocalDateTime.now());

        if (outboxRepository.exists(policy.getPolicyNumber(), tier, channel)) {
            return false;
        }

        outboxRepository.save(outbox);

        return true;
    }
}
