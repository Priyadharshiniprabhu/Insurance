package com.fd.insurance.service;

import com.fd.insurance.entity.ReminderDLQ;

import java.util.List;

public interface DLQService {

    List<ReminderDLQ> getAllDLQEvents();

    void requeue(Long id);
}