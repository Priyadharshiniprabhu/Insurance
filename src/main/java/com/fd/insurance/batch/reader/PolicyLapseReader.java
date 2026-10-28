package com.fd.insurance.batch.reader;

import com.fd.insurance.entity.Policy;
import com.fd.insurance.repository.PolicyRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemStream;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Iterator;

@Component
@StepScope
public class PolicyLapseReader implements ItemReader<Policy>, ItemStream {

    private final PolicyRepository policyRepository;
    private Iterator<Policy> policies;
    private long lastId;

    public PolicyLapseReader(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Override
    public Policy read() {
        if (policies == null || !policies.hasNext()) {
            policies = policyRepository
                    .findExpiredPoliciesForLapseAfterId(lastId, 50)
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
        policyRepository.markActivePoliciesExpired(LocalDate.now());
        lastId = context.getLong("policyLapse.lastId", 0L);
        policies = null;
    }

    @Override
    public void update(ExecutionContext context) {
        context.putLong("policyLapse.lastId", lastId);
    }

    @Override
    public void close() {
        policies = null;
    }
}
