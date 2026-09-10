package com.fd.insurance.service;

import com.fd.insurance.entity.ReminderDLQ;
import com.fd.insurance.entity.ReminderOutbox;
import com.fd.insurance.exception.DLQRecordNotFoundException;
import com.fd.insurance.repository.ReminderDLQRepository;
import com.fd.insurance.repository.ReminderOutboxRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DLQServiceImpl implements DLQService {

    private final ReminderDLQRepository dlqRepository;

    private final ReminderOutboxRepository outboxRepository;

    public DLQServiceImpl(ReminderDLQRepository dlqRepository, ReminderOutboxRepository outboxRepository) {
        this.dlqRepository = dlqRepository;
        this.outboxRepository = outboxRepository;
    }

    @Override
    public List<ReminderDLQ> getAllDLQEvents() {
        return dlqRepository.findAll();
    }

    @Override
    @Transactional
    public void requeue(Long id) {

        ReminderDLQ dlqEvent =
                dlqRepository
                        .findById(id)
                        .or(() -> dlqRepository.findByEventId(id))
                        .orElseThrow(() ->
                                new DLQRecordNotFoundException(
                                        "DLQ record not found"));

        boolean exists =
                outboxRepository.exists(
                        dlqEvent.getPolicyNumber(),
                        dlqEvent.getTier(),
                        dlqEvent.getChannel());

        if (exists) {

            throw new RuntimeException(
                    "Record is already available for processing.");
        }

        ReminderOutbox outbox =
                new ReminderOutbox();

        outbox.setPolicyNumber(
                dlqEvent.getPolicyNumber());

        outbox.setTier(
                dlqEvent.getTier());

        outbox.setChannel(dlqEvent.getChannel());

        String payload = dlqEvent.getPayload();
        if (payload == null || payload.isBlank()) {
            payload = "Policy Number : " + dlqEvent.getPolicyNumber();
        }
        outbox.setPayload(payload);

        outbox.setStatus("PENDING");

        outbox.setRetryCount(
                0);

        outbox.setNextRetryTimestamp(
                LocalDateTime.now());

        outboxRepository.save(outbox);

        dlqRepository.deleteById(dlqEvent.getId());
    }
}