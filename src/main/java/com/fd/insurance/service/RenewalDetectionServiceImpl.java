package com.fd.insurance.service;

import com.fd.insurance.entity.Policy;
import com.fd.insurance.entity.ReminderOutbox;
import com.fd.insurance.enums.ChannelType;
import com.fd.insurance.enums.PolicyStatus;
import com.fd.insurance.enums.ReminderTier;
import com.fd.insurance.repository.PolicyRepository;
import com.fd.insurance.repository.ReminderOutboxRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RenewalDetectionServiceImpl
        implements RenewalDetectionService {

    private static final Logger log =
            LoggerFactory.getLogger(RenewalDetectionServiceImpl.class);

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
        detectRenewals(policyRepository.findPoliciesForReminderDetection());
    }

    @Override
    @Transactional
    public void detectRenewals(List<? extends Policy> policies) {

        writeSummaryLog(detectRenewalsAndGetSummary(policies));
    }

    @Override
    @Transactional
    public RenewalDetectionSummary detectRenewalsAndGetSummary(
            List<? extends Policy> policies) {

        RenewalDetectionSummary summary = new RenewalDetectionSummary();
        log.info("Policies Found = {}", policies.size());

        LocalDate today = LocalDate.now();

        for (Policy policy : policies) {

            summary.incrementScannedPolicies();

            ReminderTier tier = null;

            long days = ChronoUnit.DAYS.between(today, policy.getEndDate());

            if (policy.getStatus() == PolicyStatus.ACTIVE) {

                if (days >= 0) {
                    if (days <= 7) {

                        tier = ReminderTier.SEVEN_DAY;

                    } else if (days <= 15) {

                        tier = ReminderTier.FIFTEEN_DAY;

                    } else if (days <= 30) {

                        tier = ReminderTier.THIRTY_DAY;
                    }
                }
            }

            if (policy.getStatus() == PolicyStatus.EXPIRED) {

                log.info("Expired Policy : {}", policy.getPolicyNumber());

                LocalDate graceEndDate = policy.getEndDate().plusDays(15);

                if (!today.isAfter(graceEndDate)) {

                    tier = ReminderTier.OVERDUE;

                    log.info("OVERDUE tier applied for {}", policy.getPolicyNumber());
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
                summary.incrementReminders(tier);

            } else {
                summary.incrementSkippedAlreadyReminded();
            }
        }

        return summary;
    }

    private void writeSummaryLog(RenewalDetectionSummary summary) {
        log.info("=================================");
        log.info("Renewal Detection Summary");
        log.info("Policies Scanned : {}", summary.getScannedPolicies());
        log.info("30 Day Reminders : {}", summary.getThirtyDayReminders());
        log.info("15 Day Reminders : {}", summary.getFifteenDayReminders());
        log.info("7 Day Reminders : {}", summary.getSevenDayReminders());
        log.info("Overdue Reminders : {}", summary.getOverdueReminders());
        log.info("Skipped Already Reminded : {}",
                summary.getSkippedAlreadyReminded());
        log.info("=================================");
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
