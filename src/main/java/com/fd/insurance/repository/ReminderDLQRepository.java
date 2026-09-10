package com.fd.insurance.repository;

import com.fd.insurance.entity.ReminderDLQ;

import java.util.List;
import java.util.Optional;

public interface ReminderDLQRepository {

    void save(ReminderDLQ dlq);

    List<ReminderDLQ> findAll();

    Optional<ReminderDLQ> findById(Long id);

    Optional<ReminderDLQ> findByEventId(Long eventId);

    void deleteById(Long id);
}