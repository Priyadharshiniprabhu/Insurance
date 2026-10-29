package com.fd.insurance.dto;

import java.time.LocalDateTime;
import java.util.List;

public class DashboardSummaryResponse {

    private final long totalPolicies;
    private final long active;
    private final long renewed;
    private final long overDue;
    private final FunnelSummary funnel;
    private final NotificationSummary notifications;

    public DashboardSummaryResponse(
            long totalPolicies,
            long active,
            long renewed,
            long overDue,
            FunnelSummary funnel,
            NotificationSummary notifications) {
        this.totalPolicies = totalPolicies;
        this.active = active;
        this.renewed = renewed;
        this.overDue = overDue;
        this.funnel = funnel;
        this.notifications = notifications;
    }

    public long getTotalPolicies() {
        return totalPolicies;
    }

    public long getActive() {
        return active;
    }

    public long getRenewed() {
        return renewed;
    }

    public long getOverDue() {
        return overDue;
    }

    public FunnelSummary getFunnel() {
        return funnel;
    }

    public NotificationSummary getNotifications() {
        return notifications;
    }

    public static class NotificationSummary {
        private final long sent;
        private final long failed;
        private final long invalidContact;
        private final long deadLetters;
        private final List<LatestPolicyOutcome> latestByPolicy;

        public NotificationSummary(
                long sent,
                long failed,
                long invalidContact,
                long deadLetters,
                List<LatestPolicyOutcome> latestByPolicy) {
            this.sent = sent;
            this.failed = failed;
            this.invalidContact = invalidContact;
            this.deadLetters = deadLetters;
            this.latestByPolicy = latestByPolicy;
        }

        public long getSent() {
            return sent;
        }

        public long getFailed() {
            return failed;
        }

        public long getInvalidContact() {
            return invalidContact;
        }

        public long getDeadLetters() {
            return deadLetters;
        }

        public List<LatestPolicyOutcome> getLatestByPolicy() {
            return latestByPolicy;
        }
    }

    public static class LatestPolicyOutcome {
        private final String policyNumber;
        private final String status;
        private final String reason;
        private final LocalDateTime eventTime;
        private final boolean inDeadLetterQueue;

        public LatestPolicyOutcome(
                String policyNumber,
                String status,
                String reason,
                LocalDateTime eventTime,
                boolean inDeadLetterQueue) {
            this.policyNumber = policyNumber;
            this.status = status;
            this.reason = reason;
            this.eventTime = eventTime;
            this.inDeadLetterQueue = inDeadLetterQueue;
        }

        public String getPolicyNumber() {
            return policyNumber;
        }

        public String getStatus() {
            return status;
        }

        public String getReason() {
            return reason;
        }

        public LocalDateTime getEventTime() {
            return eventTime;
        }

        public boolean isInDeadLetterQueue() {
            return inDeadLetterQueue;
        }
    }

    public static class FunnelSummary {
        private final long thirtyDay;
        private final long fifteenDay;
        private final long sevenDay;
        private final long renewed;

        public FunnelSummary(
                long thirtyDay,
                long fifteenDay,
                long sevenDay,
                long renewed) {
            this.thirtyDay = thirtyDay;
            this.fifteenDay = fifteenDay;
            this.sevenDay = sevenDay;
            this.renewed = renewed;
        }

        public long getThirtyDay() {
            return thirtyDay;
        }

        public long getFifteenDay() {
            return fifteenDay;
        }

        public long getSevenDay() {
            return sevenDay;
        }

        public long getRenewed() {
            return renewed;
        }
    }
}
