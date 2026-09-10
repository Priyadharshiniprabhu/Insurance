package com.fd.insurance.batch.reader;

import com.fd.insurance.entity.Policy;
import com.fd.insurance.repository.PolicyRepository;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class PolicyReader implements ItemReader<Policy> {

    private final PolicyRepository repository;
    private Iterator<Policy> policies;

    public PolicyReader(PolicyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Policy read() {

        if (policies == null) {
            policies = repository.findPoliciesForReminderDetection().iterator();
        }

        if (policies.hasNext()) {
            return policies.next();
        }

        return null;
    }
}