package com.fd.insurance.repository;

import com.fd.insurance.entity.ReminderOutbox;
import com.fd.insurance.enums.ChannelType;
import com.fd.insurance.enums.ReminderTier;

import java.time.LocalDateTime;
import java.util.List;

public interface ReminderOutboxRepository {
        void save(ReminderOutbox outbox);

        boolean exists(String policyNumber, ReminderTier tier, ChannelType channel);

    void deleteByPolicyNumber(String policyNumber);

    List<ReminderOutbox> findPendingEvents(int batchSize);
    List<ReminderOutbox> findPendingEventsAfterId(Long lastId, int batchSize);

        void updateStatus(Long id, String status);

        void incrementRetryCount(Long id);

        void updateNextRetryTimestamp(Long id, LocalDateTime nextRetryTimestamp);

        void resetForRetry(
                Long id,
                String payload,
                int retryCount,
                LocalDateTime nextRetryTimestamp);

        void deleteById(Long id);

        ReminderOutbox findById(Long id);
}
