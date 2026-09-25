package com.fd.insurance.dto;

public class DashboardSummaryResponse {

    private final long totalPolicies;
    private final long active;
    private final long renewed;
    private final long overDue;

    public DashboardSummaryResponse(
            long totalPolicies,
            long active,
            long renewed,
            long overDue) {
        this.totalPolicies = totalPolicies;
        this.active = active;
        this.renewed = renewed;
        this.overDue = overDue;
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
}
