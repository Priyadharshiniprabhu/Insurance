package com.fd.insurance.repository;

import com.fd.insurance.entity.ReminderOutbox;
import com.fd.insurance.enums.ChannelType;
import com.fd.insurance.enums.ReminderTier;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class ReminderOutboxRepositoryImpl implements ReminderOutboxRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(ReminderOutbox outbox) {

        entityManager.createNativeQuery("""
                INSERT INTO REMINDER_OUTBOX
                (
                    POLICY_NUMBER,
                    TIER,
                    CHANNEL,
                    PAYLOAD,
                    STATUS,
                    RETRY_COUNT,
                    NEXT_RETRY_TIMESTAMP
                )
                VALUES
                (
                    :policyNumber,
                    :tier,
                    :channel,
                    :payload,
                    :status,
                    :retryCount,
                    :nextRetryTimestamp
                )
                """)
                .setParameter("policyNumber", outbox.getPolicyNumber())
                .setParameter("tier", outbox.getTier().name())
                .setParameter("channel", outbox.getChannel().name())
                .setParameter("payload", outbox.getPayload())
                .setParameter("status", outbox.getStatus())
                .setParameter("retryCount", outbox.getRetryCount())
                .setParameter("nextRetryTimestamp", outbox.getNextRetryTimestamp())
                .executeUpdate();
    }

    @Override
    public boolean exists(String policyNumber, ReminderTier tier, ChannelType channel) {

        Number count =
                (Number) entityManager
                        .createNativeQuery("""
                            SELECT COUNT(*)
                            FROM REMINDER_OUTBOX
                            WHERE POLICY_NUMBER = :policyNumber
                            AND TIER = :tier
                            AND CHANNEL = :channel
                            """)
                        .setParameter("policyNumber", policyNumber)
                        .setParameter("tier", tier.name())
                        .setParameter("channel", channel.name())
                        .getSingleResult();

        return count.longValue() > 0;
    }

    @Override
    @Transactional
    public void deleteByPolicyNumber(String policyNumber) {

        entityManager.createNativeQuery("""
            DELETE FROM REMINDER_OUTBOX
            WHERE POLICY_NUMBER = :policyNumber
            """)
                .setParameter("policyNumber", policyNumber)
                .executeUpdate();
    }

    @Override
    public List<ReminderOutbox> findPendingEvents(int batchSize) {

        return entityManager.createQuery("""
            SELECT r
            FROM ReminderOutbox r
            WHERE r.status = 'PENDING'
            ORDER BY r.id
            """, ReminderOutbox.class)
                .setMaxResults(batchSize)
                .getResultList();
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {

        entityManager.createNativeQuery("""
            UPDATE REMINDER_OUTBOX
            SET STATUS = :status
            WHERE ID = :id
            """)
                .setParameter("status", status)
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void incrementRetryCount(Long id) {

        entityManager.createNativeQuery("""
            UPDATE REMINDER_OUTBOX
            SET RETRY_COUNT = RETRY_COUNT + 1
            WHERE ID = :id
            """)
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        entityManager.createNativeQuery("""
            DELETE FROM REMINDER_OUTBOX
            WHERE ID = :id
            """)
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public ReminderOutbox findById(Long id) {

        return entityManager.find(ReminderOutbox.class, id);
    }
}
