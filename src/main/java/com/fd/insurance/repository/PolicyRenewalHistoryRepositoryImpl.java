package com.fd.insurance.repository;

import com.fd.insurance.entity.PolicyRenewalHistory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@Transactional
public class PolicyRenewalHistoryRepositoryImpl
        implements PolicyRenewalHistoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(PolicyRenewalHistory history) {
        entityManager.persist(history);
    }

    @Override
    public long countRenewalsBetween(LocalDateTime start, LocalDateTime end) {
        return entityManager.createQuery("""
                SELECT COUNT(h)
                FROM PolicyRenewalHistory h
                WHERE h.renewedAt >= :start
                AND h.renewedAt < :end
                """, Long.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult();
    }
}
