package com.fd.insurance.repository;

import com.fd.insurance.entity.PolicyRenewalHistory;

import java.time.LocalDateTime;

public interface PolicyRenewalHistoryRepository {

    void save(PolicyRenewalHistory history);

    long countRenewalsBetween(LocalDateTime start, LocalDateTime end);
}
