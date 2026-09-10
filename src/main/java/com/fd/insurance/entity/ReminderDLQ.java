package com.fd.insurance.entity;

import com.fd.insurance.enums.ChannelType;
import com.fd.insurance.enums.ReminderTier;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "REMINDER_DLQ")
public class ReminderDLQ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "EVENT_ID")
    private Long eventId;

    @Column(name = "POLICY_NUMBER")
    private String policyNumber;

    @Column(name = "PAYLOAD", length = 2000)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "CHANNEL")
    private ChannelType channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIER")
    private ReminderTier tier;

    @Column(name = "RETRY_COUNT")
    private Integer retryCount;

    @Column(name = "LAST_ERROR")
    private String lastError;

    @Column(name = "MOVED_AT")
    private LocalDateTime movedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
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

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public LocalDateTime getMovedAt() {
        return movedAt;
    }

    public void setMovedAt(LocalDateTime movedAt) {
        this.movedAt = movedAt;
    }
}