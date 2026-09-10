package com.fd.insurance.dto;

import com.fd.insurance.enums.ChannelType;
import com.fd.insurance.enums.NotificationOutcome;
import com.fd.insurance.enums.ReminderTier;

import java.time.LocalDateTime;

public class ReminderHistoryResponse {

    private Long eventId;

    private ReminderTier tier;

    private ChannelType channel;

    private LocalDateTime eventTime;

    private NotificationOutcome outcome;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public ReminderTier getTier() {
        return tier;
    }

    public void setTier(ReminderTier tier) {
        this.tier = tier;
    }

    public ChannelType getChannel() {
        return channel;
    }

    public void setChannel(ChannelType channel) {
        this.channel = channel;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public NotificationOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(NotificationOutcome outcome) {
        this.outcome = outcome;
    }
}