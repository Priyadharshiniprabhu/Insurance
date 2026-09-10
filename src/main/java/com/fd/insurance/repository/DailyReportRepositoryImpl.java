package com.fd.insurance.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DailyReportRepositoryImpl implements DailyReportRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> countPoliciesByStatus() {
        return entityManager.createNativeQuery("""
                SELECT STATUS, COUNT(*)
                FROM POLICIES
                GROUP BY STATUS
                """).getResultList();
    }

    @Override
    public List<Object[]> countNotificationsByTierAndOutcome(
            LocalDateTime start,
            LocalDateTime end) {
        return entityManager.createNativeQuery("""
                SELECT TIER, OUTCOME, COUNT(*)
                FROM NOTIFICATION_LOG
                WHERE EVENT_TIME >= :start
                AND EVENT_TIME < :end
                GROUP BY TIER, OUTCOME
                """)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    @Override
    public List<Object[]> countDeadLettersByTierAndReason(
            LocalDateTime start,
            LocalDateTime end) {
        return entityManager.createNativeQuery("""
                SELECT TIER, LAST_ERROR, COUNT(*)
                FROM REMINDER_DLQ
                WHERE MOVED_AT >= :start
                AND MOVED_AT < :end
                GROUP BY TIER, LAST_ERROR
                """)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}
