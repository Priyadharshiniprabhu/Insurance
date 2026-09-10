package com.fd.insurance.repository;

import com.fd.insurance.entity.ReminderDLQ;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ReminderDLQRepositoryImpl implements ReminderDLQRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(ReminderDLQ dlq) {
        entityManager.persist(dlq);
    }

    @Override
    public List<ReminderDLQ> findAll() {

        return entityManager
                .createQuery("""
                SELECT d
                FROM ReminderDLQ d
                ORDER BY d.movedAt DESC
                """,
                        ReminderDLQ.class)
                .getResultList();
    }

    @Override
    public Optional<ReminderDLQ> findById(Long id) {

        ReminderDLQ dlq = entityManager.find(ReminderDLQ.class, id);

        return Optional.ofNullable(dlq);
    }

    @Override
    public Optional<ReminderDLQ> findByEventId(Long eventId) {

        return entityManager
                .createQuery("""
                SELECT d
                FROM ReminderDLQ d
                WHERE d.eventId = :eventId
                """, ReminderDLQ.class)
                .setParameter("eventId", eventId)
                .getResultStream()
                .findFirst();
    }

    @Override
    public void deleteById(Long id) {

        entityManager
                .createNativeQuery("""
                DELETE FROM REMINDER_DLQ
                WHERE ID = :id
                """)
                .setParameter("id", id)
                .executeUpdate();
    }

}
