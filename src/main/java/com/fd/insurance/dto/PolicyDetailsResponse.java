package com.fd.insurance.dto;

import com.fd.insurance.enums.PolicyStatus;
import com.fd.insurance.enums.PolicyType;

import com.fd.insurance.dto.ReminderHistoryResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PolicyDetailsResponse {

    private String policyNumber;

    private String holderName;

    private String email;

    private String phone;

    private PolicyType policyType;

    private BigDecimal premiumAmount;

    private LocalDate startDate;

    private LocalDate endDate;

    private PolicyStatus status;

    private List<ReminderHistoryResponse> reminderHistory;

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public PolicyType getPolicyType() {
        return policyType;
    }

    public void setPolicyType(PolicyType policyType) {
        this.policyType = policyType;
    }

    public BigDecimal getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(BigDecimal premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public void setStatus(PolicyStatus status) {
        this.status = status;
    }

    public List<ReminderHistoryResponse> getReminderHistory() {
        return reminderHistory;
    }

    public void setReminderHistory(List<ReminderHistoryResponse> reminderHistory) {
        this.reminderHistory = reminderHistory;
    }
}