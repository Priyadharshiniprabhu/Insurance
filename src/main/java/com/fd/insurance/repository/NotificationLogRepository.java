package com.fd.insurance.repository;

import com.fd.insurance.entity.NotificationLog;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationLogRepository {
    void save(NotificationLog log);

//    List<NotificationLog> findByPolicyNumber(String policyNumber);
    List<Object[]> findHistoryByPolicyNumber(String policyNumber);
    List<Object[]> countOutcomesBetween(LocalDateTime start, LocalDateTime end);
}
