package com.fd.insurance.repository;

import com.fd.insurance.entity.SchedulerConfiguration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class SchedulerConfigurationRepositoryImpl
        implements SchedulerConfigurationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<SchedulerConfiguration> findByName(String name) {
        List<SchedulerConfiguration> configurations = entityManager
                .createQuery("""
                        SELECT configuration
                        FROM SchedulerConfiguration configuration
                        WHERE configuration.name = :name
                        """, SchedulerConfiguration.class)
                .setParameter("name", name)
                .getResultList();

        return configurations.stream().findFirst();
    }

    @Override
    public void save(SchedulerConfiguration configuration) {
        entityManager.persist(configuration);
    }
}
