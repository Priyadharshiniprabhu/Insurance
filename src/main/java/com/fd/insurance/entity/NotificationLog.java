package com.fd.insurance.entity;

import com.fd.insurance.enums.ChannelType;
import com.fd.insurance.enums.NotificationOutcome;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "OUTCOME")
    private NotificationOutcome outcome;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "POLICY_NUMBER")
    private String policyNumber;

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

    public NotificationOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(NotificationOutcome outcome) {
        this.outcome = outcome;
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
}