package com.fd.insurance.batch.writer;

import com.fd.insurance.entity.ReminderOutbox;
import com.fd.insurance.service.NotificationDispatchService;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class NotificationWriter implements ItemWriter<ReminderOutbox> {

    private final NotificationDispatchService service;

    public NotificationWriter(NotificationDispatchService service) {
        this.service = service;
    }

    @Override
    public void write(Chunk<? extends ReminderOutbox> chunk) {
        service.processPendingEvents();
    }
}