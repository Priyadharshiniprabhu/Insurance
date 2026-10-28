package com.fd.insurance.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalDate;
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
    public List<Object[]> countPoliciesForFunnel(LocalDate reportDate) {
        return entityManager.createNativeQuery("""
                SELECT 'RENEWED', COUNT(*)
                FROM POLICIES
                WHERE STATUS = 'ACTIVE'
                  AND UPDATED_DATE = :renewedDate
                UNION ALL
                SELECT 'ACTIVE', COUNT(*)
                FROM POLICIES
                WHERE STATUS = 'ACTIVE'
                  AND (UPDATED_DATE <> :activeDate OR UPDATED_DATE IS NULL)
                """)
                .setParameter("renewedDate", reportDate)
                .setParameter("activeDate", reportDate)
                .getResultList();
    }

    @Override
    public List<Object[]> countNotificationsByTierAndOutcome(
            LocalDateTime start,
            LocalDateTime end) {
        return entityManager.createNativeQuery("""
                SELECT TIER, STATUS, REASON, COUNT(*)
                FROM NOTIFICATION_LOG
                WHERE EVENT_TIME >= :start
                AND EVENT_TIME < :end
                GROUP BY TIER, STATUS, REASON
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

    @Override
    public List<Object[]> countInvalidContactsByTier(
            LocalDateTime start,
            LocalDateTime end) {
        return entityManager.createNativeQuery("""
                SELECT TIER, COUNT(*)
                FROM NOTIFICATION_LOG
                WHERE EVENT_TIME >= :start
                AND EVENT_TIME < :end
                AND REASON = 'INVALID_CONTACT'
                GROUP BY TIER
                """)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    @Override
    public List<Object[]> countDeadLettersByTier(
            LocalDateTime start,
            LocalDateTime end) {
        return entityManager.createNativeQuery("""
                SELECT TIER, COUNT(*)
                FROM REMINDER_DLQ
                WHERE MOVED_AT >= :start
                AND MOVED_AT < :end
                GROUP BY TIER
                """)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}
