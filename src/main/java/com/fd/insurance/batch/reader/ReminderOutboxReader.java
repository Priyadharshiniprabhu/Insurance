package com.fd.insurance.batch.reader;

import com.fd.insurance.entity.ReminderOutbox;
import com.fd.insurance.repository.ReminderOutboxRepository;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class ReminderOutboxReader implements ItemReader<ReminderOutbox> {

    private final ReminderOutboxRepository repository;
    private Iterator<ReminderOutbox> events;

    public ReminderOutboxReader(ReminderOutboxRepository repository) {
        this.repository = repository;
    }

    @Override
    public ReminderOutbox read() {

        if (events == null) {
            events = repository.findPendingEvents(50).iterator();
        }

        if (events.hasNext()) {
            return events.next();
        }

        return null;
    }
}