package com.fd.insurance.batch.writer;

import com.fd.insurance.entity.Policy;
import com.fd.insurance.service.RenewalDetectionService;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class RenewalDetectionWriter implements ItemWriter<Policy> {

    private final RenewalDetectionService renewalDetectionService;

    public RenewalDetectionWriter(RenewalDetectionService renewalDetectionService) {
        this.renewalDetectionService = renewalDetectionService;
    }

    @Override
    public void write(Chunk<? extends Policy> chunk) {
        renewalDetectionService.detectRenewals(chunk.getItems());
    }
}
