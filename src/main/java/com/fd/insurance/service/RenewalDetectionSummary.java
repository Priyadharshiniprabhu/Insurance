package com.fd.insurance.service;

public class RenewalDetectionSummary {

    private int scannedPolicies;
    private int thirtyDayReminders;
    private int fifteenDayReminders;
    private int sevenDayReminders;
    private int overdueReminders;
    private int skippedAlreadyReminded;

    public void reset() {
        scannedPolicies = 0;
        thirtyDayReminders = 0;
        fifteenDayReminders = 0;
        sevenDayReminders = 0;
        overdueReminders = 0;
        skippedAlreadyReminded = 0;
    }

    public void add(RenewalDetectionSummary summary) {
        scannedPolicies += summary.scannedPolicies;
        thirtyDayReminders += summary.thirtyDayReminders;
        fifteenDayReminders += summary.fifteenDayReminders;
        sevenDayReminders += summary.sevenDayReminders;
        overdueReminders += summary.overdueReminders;
        skippedAlreadyReminded += summary.skippedAlreadyReminded;
    }

    public void incrementScannedPolicies() {
        scannedPolicies++;
    }

    public void incrementReminders(com.fd.insurance.enums.ReminderTier tier) {
        switch (tier) {
            case THIRTY_DAY -> thirtyDayReminders++;
            case FIFTEEN_DAY -> fifteenDayReminders++;
            case SEVEN_DAY -> sevenDayReminders++;
            case OVERDUE -> overdueReminders++;
        }
    }

    public void incrementSkippedAlreadyReminded() {
        skippedAlreadyReminded++;
    }

    public int getScannedPolicies() {
        return scannedPolicies;
    }

    public int getThirtyDayReminders() {
        return thirtyDayReminders;
    }

    public int getFifteenDayReminders() {
        return fifteenDayReminders;
    }

    public int getSevenDayReminders() {
        return sevenDayReminders;
    }

    public int getOverdueReminders() {
        return overdueReminders;
    }

    public int getSkippedAlreadyReminded() {
        return skippedAlreadyReminded;
    }
}
