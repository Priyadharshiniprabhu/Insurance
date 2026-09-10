package com.fd.insurance.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class PolicyRenewRequest {

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}