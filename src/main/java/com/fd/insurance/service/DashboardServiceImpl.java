package com.fd.insurance.service;

import com.fd.insurance.dto.DashboardSummaryResponse;
import com.fd.insurance.repository.PolicyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final PolicyRepository policyRepository;

    public DashboardServiceImpl(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Override
    public DashboardSummaryResponse getSummary() {
        LocalDate today = LocalDate.now();
        return new DashboardSummaryResponse(
                policyRepository.countAll(),
                policyRepository.countActiveExcludingUpdatedDate(today),
                policyRepository.countActiveUpdatedOn(today),
                policyRepository.countOverdue());
    }
}
