package com.fd.insurance.service;

import com.fd.insurance.entity.NotificationLog;
import com.fd.insurance.entity.ReminderDLQ;
import com.fd.insurance.entity.ReminderOutbox;
import com.fd.insurance.enums.NotificationOutcome;
import com.fd.insurance.enums.PolicyStatus;
import com.fd.insurance.repository.NotificationLogRepository;
import com.fd.insurance.repository.PolicyRepository;
import com.fd.insurance.repository.ReminderDLQRepository;
import com.fd.insurance.repository.ReminderOutboxRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class NotificationDispatchServiceImpl implements NotificationDispatchService {

    private final ReminderOutboxRepository outboxRepository;
    private final NotificationLogRepository logRepository;
    private final PolicyRepository policyRepository;

    private final Random random = new Random();

    private final ReminderDLQRepository dlqRepository;

    public NotificationDispatchServiceImpl(
            ReminderOutboxRepository outboxRepository,
            NotificationLogRepository logRepository,
            ReminderDLQRepository dlqRepository,
            PolicyRepository policyRepository) {

        this.outboxRepository = outboxRepository;
        this.logRepository = logRepository;
        this.dlqRepository = dlqRepository;
        this.policyRepository = policyRepository;
    }

    @Override
    @Transactional
    public void processPendingEvents() {
        List<ReminderOutbox> events = outboxRepository.findPendingEvents(50);
        processPendingEvents(events);
    }

    @Override
    @Transactional
    public void processPendingEvents(List<? extends ReminderOutbox> events) {

        for (ReminderOutbox event : events) {
            if (policyRepository.findByPolicyNumber(event.getPolicyNumber())
                    .map(policy -> policy.getStatus() == PolicyStatus.ACTIVE)
                    .orElse(true)) {
                outboxRepository.deleteById(event.getId());
                continue;
            }

            var policy = policyRepository.findByPolicyNumber(event.getPolicyNumber())
                    .orElse(null);
            if (policy == null) {
                continue;
            }

            NotificationOutcome outcome;
            if (hasInvalidContact(policy, event)) {
                outcome = NotificationOutcome.INVALID_CONTACT;
            } else {
                int number = random.nextInt(3);

                if (number == 0) {
                    outcome = NotificationOutcome.SENT;
                } else if (number == 1) {
                    outcome = NotificationOutcome.FAILED;
                } else {
                    outcome = NotificationOutcome.GATEWAY_TIMEOUT;
                }
            }

            NotificationLog log = new NotificationLog();

            log.setEventId(event.getId());
            log.setChannel(event.getChannel());
            log.setTier(event.getTier());
            log.setOutcome(outcome);
            log.setEventTime(LocalDateTime.now());
            log.setPolicyNumber(event.getPolicyNumber());
            log.setReason(outcome.name());

            logRepository.save(log);

            if (outcome == NotificationOutcome.SENT) {
                outboxRepository.updateStatus(event.getId(), "SENT");

            } else if (outcome == NotificationOutcome.INVALID_CONTACT) {
                outboxRepository.updateStatus(event.getId(), "FAILED");
                policyRepository.flagForManualFollowUp(event.getPolicyNumber());

            } else {

                outboxRepository.incrementRetryCount(event.getId());

                int retryCount = event.getRetryCount() + 1;

                if (retryCount >= 3) {
                    outboxRepository.updateStatus(event.getId(), "FAILED");

                    ReminderDLQ dlq = new ReminderDLQ();

                    dlq.setEventId(event.getId());
                    dlq.setPolicyNumber(event.getPolicyNumber());
                    dlq.setPayload(event.getPayload());
                    dlq.setChannel(event.getChannel());
                    dlq.setTier(event.getTier());
                    dlq.setRetryCount(retryCount);
                    dlq.setLastError(outcome.name());
                    dlq.setMovedAt(LocalDateTime.now());

                    dlqRepository.save(dlq);
                } else {
                    long backoffMinutes = 1L << (retryCount - 1);
                    outboxRepository.updateNextRetryTimestamp(
                            event.getId(),
                            LocalDateTime.now().plusMinutes(backoffMinutes));
                }
            }
        }
    }

    private boolean hasInvalidContact(
            com.fd.insurance.entity.Policy policy,
            ReminderOutbox event) {

        if (event.getChannel().name().equals("EMAIL")) {
            return policy.getEmail() == null
                    || !policy.getEmail().matches(
                            "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
        }

        return policy.getPhone() == null
                || !policy.getPhone().matches("^\\+?[0-9 ()-]{7,20}$");
    }
}