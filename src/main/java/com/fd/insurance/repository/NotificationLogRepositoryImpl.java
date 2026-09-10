package com.fd.insurance.repository;

import com.fd.insurance.entity.NotificationLog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

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
                    OUTCOME
                FROM NOTIFICATION_LOG
                WHERE POLICY_NUMBER = :policyNumber
                ORDER BY EVENT_TIME
                """)
                .setParameter("policyNumber", policyNumber)
                .getResultList();
    }
}