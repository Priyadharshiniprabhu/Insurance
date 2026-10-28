package com.fd.insurance.batch.reader;

import com.fd.insurance.entity.Policy;
import com.fd.insurance.repository.PolicyRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemStream;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
@StepScope
public class RenewalDetectionReader implements ItemReader<Policy>, ItemStream {

    private final PolicyRepository policyRepository;
    private Iterator<Policy> policies;
    private long lastId;

    public RenewalDetectionReader(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Override
    public Policy read() {
        if (policies == null || !policies.hasNext()) {
            policies = policyRepository
                    .findPoliciesForReminderDetectionAfterId(lastId, 50)
                    .iterator();
        }

        if (!policies.hasNext()) {
            return null;
        }

        Policy policy = policies.next();
        lastId = policy.getId();
        return policy;
    }

    @Override
    public void open(ExecutionContext context) {
        lastId = context.getLong("renewalDetection.lastId", 0L);
        policies = null;
    }

    @Override
    public void update(ExecutionContext context) {
        context.putLong("renewalDetection.lastId", lastId);
    }

    @Override
    public void close() {
        policies = null;
    }
}
