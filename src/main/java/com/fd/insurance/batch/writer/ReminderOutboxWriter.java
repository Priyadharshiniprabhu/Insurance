package com.fd.insurance.batch.writer;

import com.fd.insurance.entity.Policy;
import com.fd.insurance.service.RenewalDetectionService;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class ReminderOutboxWriter implements ItemWriter<Policy> {

    private final RenewalDetectionService service;

    public ReminderOutboxWriter(RenewalDetectionService service) {
        this.service = service;
    }

    @Override
    public void write(Chunk<? extends Policy> chunk) {
        service.detectRenewals();
    }
}