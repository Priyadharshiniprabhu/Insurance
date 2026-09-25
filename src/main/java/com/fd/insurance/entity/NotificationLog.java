package com.fd.insurance.entity;

import com.fd.insurance.enums.ChannelType;
import com.fd.insurance.enums.NotificationReason;
import com.fd.insurance.enums.NotificationStatus;
import com.fd.insurance.enums.ReminderTier;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "NOTIFICATION_LOG")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "EVENT_ID")
    private Long eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "CHANNEL")
    private ChannelType channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIER")
    private ReminderTier tier;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "POLICY_NUMBER")
    private String policyNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "REASON")
    private NotificationReason reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private NotificationStatus status;

// getters/setters

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

    public ChannelType getChannel() {
        return channel;
    }

    public void setChannel(ChannelType channel) {
        this.channel = channel;
    }

    public ReminderTier getTier() {
        return tier;
    }

    public void setTier(ReminderTier tier) {
        this.tier = tier;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getPolicyNumber(){
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber){
        this.policyNumber = policyNumber;
    }

    public NotificationReason getReason() {
        return reason;
    }

    public void setReason(NotificationReason reason) {
        this.reason = reason;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public NotificationStatus getStatus() {
        return status;
    }
}