package com.fd.insurance.repository;

import com.fd.insurance.entity.SchedulerConfiguration;

import java.util.Optional;

public interface SchedulerConfigurationRepository {

    Optional<SchedulerConfiguration> findByName(String name);

    void save(SchedulerConfiguration configuration);
}
