package com.fd.insurance.batch.reader;

import com.fd.insurance.entity.ReminderOutbox;
import com.fd.insurance.repository.ReminderOutboxRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemStream;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
@StepScope
public class ReminderOutboxReader implements ItemReader<ReminderOutbox>, ItemStream {

    private final ReminderOutboxRepository repository;
    private Iterator<ReminderOutbox> events;
    private long lastId;

    public ReminderOutboxReader(ReminderOutboxRepository repository) {
        this.repository = repository;
    }

    @Override
    public ReminderOutbox read() {
        if (events == null || !events.hasNext()) {
            events = repository
                    .findPendingEventsAfterId(lastId, 50)
                    .iterator();
        }

        if (!events.hasNext()) {
            return null;
        }

        ReminderOutbox event = events.next();
        lastId = event.getId();
        return event;
    }

    @Override
    public void open(ExecutionContext context) {
        lastId = context.getLong("notification.lastId", 0L);
        events = null;
    }

    @Override
    public void update(ExecutionContext context) {
        context.putLong("notification.lastId", lastId);
    }

    @Override
    public void close() {
        events = null;
    }
}