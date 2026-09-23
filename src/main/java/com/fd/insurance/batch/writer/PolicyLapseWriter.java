package com.fd.insurance.batch.writer;

import com.fd.insurance.entity.Policy;
import com.fd.insurance.service.PolicyLapseService;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class PolicyLapseWriter implements ItemWriter<Policy> {

    private final PolicyLapseService policyLapseService;

    public PolicyLapseWriter(PolicyLapseService policyLapseService) {
        this.policyLapseService = policyLapseService;
    }

    @Override
    public void write(Chunk<? extends Policy> chunk) {
        policyLapseService.markLapsedPolicies(chunk.getItems());
    }
}
