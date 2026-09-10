package com.fd.insurance.repository;

import com.fd.insurance.entity.Policy;
import com.fd.insurance.enums.PolicyStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PolicyRepository {
    void save(Policy policy);

    Optional<Policy> findByPolicyNumber(String policyNumber);
    List<Policy> findPoliciesForReminderDetection();

    void updatePolicyForRenewal(String policyNumber, LocalDate endDate, PolicyStatus status);

    void updatePolicyStatus(String policyNumber, PolicyStatus status);

    Optional<Policy> findByEmail(String email);

    Optional<Policy> findByPhone(String phone);

    List<Policy> findExpiredPoliciesForLapse();

}
