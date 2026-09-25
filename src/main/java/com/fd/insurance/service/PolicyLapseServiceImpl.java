package com.fd.insurance.service;

import com.fd.insurance.entity.Policy;
import com.fd.insurance.enums.PolicyStatus;
import com.fd.insurance.repository.PolicyRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class PolicyLapseServiceImpl implements PolicyLapseService {

    private static final Logger JOB_LOGGER =
            LoggerFactory.getLogger("POLICY_LAPSE_JOB");

    private final PolicyRepository policyRepository;

    public PolicyLapseServiceImpl(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Override
    @Transactional
    public void markLapsedPolicies() {
        markLapsedPolicies(policyRepository.findExpiredPoliciesForLapse());
    }

    @Override
    @Transactional
    public void markLapsedPolicies(List<? extends Policy> policies) {
        LocalDate today = LocalDate.now();

        for (Policy policy : policies) {
            if (today.isAfter(policy.getEndDate().plusDays(15))) {
                int updatedRows = policyRepository.markLapsedIfExpired(
                        policy.getPolicyNumber(),
                        today.minusDays(15));

                if (updatedRows == 1) {
                    JOB_LOGGER.info(
                            "Policy {} marked as LAPSED",
                            policy.getPolicyNumber());
                }
            }
        }
    }
}
