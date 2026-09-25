package com.fd.insurance.batch.processor;

import com.fd.insurance.entity.Policy;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class RenewalDetectionBatchProcessor implements ItemProcessor<Policy, Policy> {

    @Override
    public Policy process(Policy policy) {
        return policy;
    }
}
