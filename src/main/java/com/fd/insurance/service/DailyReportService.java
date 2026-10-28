package com.fd.insurance.service;

import java.io.IOException;
import java.time.LocalDate;

public interface DailyReportService {

    void generateReport(LocalDate reportDate);

    byte[] generateAndReadReport(LocalDate reportDate) throws IOException;
}
