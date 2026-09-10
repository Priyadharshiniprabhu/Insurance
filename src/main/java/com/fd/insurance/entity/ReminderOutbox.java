package com.fd.insurance.entity;

import com.fd.insurance.enums.ChannelType;
import com.fd.insurance.enums.ReminderTier;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "REMINDER_OUTBOX",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_POLICY_TIER_CHANNEL",
                        columnNames = {"POLICY_NUMBER", "TIER", "CHANNEL"}
                )
        }
)
public class ReminderOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "POLICY_NUMBER", nullable = false)
    private String policyNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIER", nullable = false)
    private ReminderTier tier;

    @Enumerated(EnumType.STRING)
    @Column(name = "CHANNEL", nullable = false)
    private ChannelType channel;

    @Column(name = "PAYLOAD", nullable = false, length = 2000)
    private String payload;

    @Column(name = "STATUS", nullable = false)
    private String status;

    @Column(name = "RETRY_COUNT", nullable = false)
    private Integer retryCount;

    @Column(name = "NEXT_RETRY_TIMESTAMP")
    private LocalDateTime nextRetryTimestamp;

    public ReminderOutbox() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
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

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public LocalDateTime getNextRetryTimestamp() {
        return nextRetryTimestamp;
    }

    public void setNextRetryTimestamp(LocalDateTime nextRetryTimestamp) {
        this.nextRetryTimestamp = nextRetryTimestamp;
    }
}