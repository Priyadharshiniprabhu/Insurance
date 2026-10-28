package com.fd.insurance.repository;

import com.fd.insurance.entity.NotificationLog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class NotificationLogRepositoryImpl implements NotificationLogRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(NotificationLog log) {

        entityManager.persist(log);
    }

    @Override
    public List<Object[]> findHistoryByPolicyNumber(
            String policyNumber) {

        return entityManager
                .createNativeQuery("""
                SELECT
                    EVENT_ID,
                    CHANNEL,
                    TIER,
                    EVENT_TIME,
                    STATUS,
                    REASON
                FROM NOTIFICATION_LOG
                WHERE POLICY_NUMBER = :policyNumber
                ORDER BY EVENT_TIME
                """)
                .setParameter("policyNumber", policyNumber)
                .getResultList();
    }

    @Override
    public List<Object[]> countOutcomesBetween(
            LocalDateTime start,
            LocalDateTime end) {
        return entityManager.createQuery("""
                SELECT log.status, log.reason, COUNT(log)
                FROM NotificationLog log
                WHERE log.eventTime >= :start
                  AND log.eventTime < :end
                GROUP BY log.status, log.reason
                """, Object[].class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}