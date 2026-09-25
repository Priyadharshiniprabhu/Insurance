package com.fd.insurance.repository;

import com.fd.insurance.entity.Policy;
import com.fd.insurance.enums.PolicyStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PolicyRepository {
    void save(Policy policy);

    long countAll();
    long countByStatus(PolicyStatus status);
    long countActiveExcludingUpdatedDate(LocalDate date);
    long countActiveUpdatedOn(LocalDate date);
    long countOverdue();

    Optional<Policy> findByPolicyNumber(String policyNumber);
    Optional<Policy> findByPolicyNumberForUpdate(String policyNumber);
    List<Policy> findPoliciesForReminderDetection();
    List<Policy> findPoliciesForReminderDetectionAfterId(Long lastId, int batchSize);

    void updatePolicyForRenewal(String policyNumber, LocalDate endDate, PolicyStatus status);

    void updatePolicyStatus(String policyNumber, PolicyStatus status);
    int markLapsedIfExpired(String policyNumber, LocalDate endDateBefore);

    void flagForManualFollowUp(String policyNumber);

    Optional<Policy> findByEmail(String email);

    Optional<Policy> findByPhone(String phone);

    List<Policy> findExpiredPoliciesForLapse();
    List<Policy> findExpiredPoliciesForLapseAfterId(Long lastId, int batchSize);
    int markActivePoliciesExpired(LocalDate today);

}
