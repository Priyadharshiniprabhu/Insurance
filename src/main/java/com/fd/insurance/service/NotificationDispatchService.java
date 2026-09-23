package com.fd.insurance.service;

import com.fd.insurance.entity.ReminderOutbox;

import java.util.List;

public interface NotificationDispatchService {
    void processPendingEvents();

    void processPendingEvents(List<? extends ReminderOutbox> events);
}
