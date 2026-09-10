package com.fd.insurance.repository;

import com.fd.insurance.entity.ReminderOutbox;
import com.fd.insurance.enums.ChannelType;
import com.fd.insurance.enums.ReminderTier;

import java.util.List;

public interface ReminderOutboxRepository {
        void save(ReminderOutbox outbox);

        boolean exists(String policyNumber, ReminderTier tier, ChannelType channel);

        void deleteByPolicyNumber(String policyNumber);

        List<ReminderOutbox> findPendingEvents(int batchSize);

        void updateStatus(Long id, String status);

        void incrementRetryCount(Long id);

        void deleteById(Long id);

        ReminderOutbox findById(Long id);
}
