package com.fd.insurance.service;

import java.time.LocalDate;

public interface DailyReportService {

    void generateReport(LocalDate reportDate);
}
