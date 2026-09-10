package com.fd.insurance.service;

import com.fd.insurance.entity.NotificationLog;
import com.fd.insurance.entity.ReminderDLQ;
import com.fd.insurance.entity.ReminderOutbox;
import com.fd.insurance.enums.NotificationOutcome;
import com.fd.insurance.repository.NotificationLogRepository;
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

    private final Random random = new Random();

    private final ReminderDLQRepository dlqRepository;

    public NotificationDispatchServiceImpl(
            ReminderOutboxRepository outboxRepository,
            NotificationLogRepository logRepository,
            ReminderDLQRepository dlqRepository) {

        this.outboxRepository = outboxRepository;
        this.logRepository = logRepository;
        this.dlqRepository = dlqRepository;
    }

    @Override
    @Transactional
    public void processPendingEvents() {

        List<ReminderOutbox> events = outboxRepository.findPendingEvents(50);

        for (ReminderOutbox event : events) {

            int number = random.nextInt(3);

            NotificationOutcome outcome;

            if (number == 0) {
                outcome = NotificationOutcome.SENT;

            } else if (number == 1) {
                outcome = NotificationOutcome.FAILED;

            } else {
                outcome = NotificationOutcome.GATEWAY_TIMEOUT;
            }

            NotificationLog log = new NotificationLog();

            log.setEventId(event.getId());
            log.setChannel(event.getChannel());
            log.setTier(event.getTier());
            log.setOutcome(outcome);
            log.setEventTime(LocalDateTime.now());
            log.setPolicyNumber(event.getPolicyNumber());

            logRepository.save(log);

            if (outcome == NotificationOutcome.SENT) {
                outboxRepository.deleteById(event.getId());

            } else {

                outboxRepository.incrementRetryCount(event.getId());

                ReminderOutbox updatedEvent = outboxRepository.findById(event.getId());

                if (updatedEvent.getRetryCount() >= 3) {

                    ReminderDLQ dlq = new ReminderDLQ();

                    dlq.setEventId(updatedEvent.getId());
                    dlq.setPolicyNumber(updatedEvent.getPolicyNumber());
                    dlq.setPayload(updatedEvent.getPayload());
                    dlq.setChannel(updatedEvent.getChannel());
                    dlq.setTier(updatedEvent.getTier());
                    dlq.setRetryCount(updatedEvent.getRetryCount());
                    dlq.setLastError(outcome.name());
                    dlq.setMovedAt(LocalDateTime.now());

                    dlqRepository.save(dlq);

                    outboxRepository.deleteById(updatedEvent.getId());
                }
            }
        }
    }
}