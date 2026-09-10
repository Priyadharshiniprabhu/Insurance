package com.fd.insurance.batch.processor;

import com.fd.insurance.entity.Policy;
import com.fd.insurance.enums.PolicyStatus;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class RenewalDetectionProcessor implements ItemProcessor<Policy, Policy> {

    @Override
    public Policy process(Policy policy) {

        long days = ChronoUnit.DAYS.between(LocalDate.now(), policy.getEndDate());

        if (policy.getStatus() == PolicyStatus.ACTIVE) {

            if (days == 30 || days == 15 || days == 7) {
                policy.setStatus(PolicyStatus.RENEWAL_DUE);
            }
        }

        return policy;
    }
}