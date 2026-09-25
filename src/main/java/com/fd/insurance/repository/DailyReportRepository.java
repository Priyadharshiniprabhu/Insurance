package com.fd.insurance.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DailyReportRepository {

    List<Object[]> countPoliciesByStatus();

    List<Object[]> countPoliciesForFunnel(LocalDate reportDate);

    List<Object[]> countNotificationsByTierAndOutcome(
            LocalDateTime start,
            LocalDateTime end);

    List<Object[]> countDeadLettersByTierAndReason(
            LocalDateTime start,
            LocalDateTime end);

    List<Object[]> countInvalidContactsByTier(
            LocalDateTime start,
            LocalDateTime end);

    List<Object[]> countDeadLettersByTier(
            LocalDateTime start,
            LocalDateTime end);
}
