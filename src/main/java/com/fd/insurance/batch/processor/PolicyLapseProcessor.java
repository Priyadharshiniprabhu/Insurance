package com.fd.insurance.batch.processor;

import com.fd.insurance.entity.Policy;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PolicyLapseProcessor implements ItemProcessor<Policy, Policy> {

    @Override
    public Policy process(Policy policy) {
        return LocalDate.now().isAfter(policy.getEndDate().plusDays(15))
                ? policy
                : null;
    }
}
