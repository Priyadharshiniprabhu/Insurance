package com.fd.insurance.batch.processor;

import com.fd.insurance.entity.ReminderOutbox;
import com.fd.insurance.enums.PolicyStatus;
import com.fd.insurance.repository.PolicyRepository;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class NotificationProcessor implements ItemProcessor<ReminderOutbox, ReminderOutbox> {

    private final PolicyRepository policyRepository;

    public NotificationProcessor(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Override
    public ReminderOutbox process(ReminderOutbox item) {
        return policyRepository.findByPolicyNumber(item.getPolicyNumber())
                .filter(policy -> policy.getStatus() != PolicyStatus.ACTIVE)
                .map(policy -> item)
                .orElse(null);
    }
}