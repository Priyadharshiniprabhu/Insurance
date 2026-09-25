package com.fd.insurance.service;

import com.fd.insurance.repository.DailyReportRepository;
import com.fd.insurance.repository.PolicyRenewalHistoryRepository;
import com.fd.insurance.enums.ReminderTier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Service
public class DailyReportServiceImpl implements DailyReportService {

    private final DailyReportRepository reportRepository;
    private final PolicyRenewalHistoryRepository renewalHistoryRepository;
    private final Path reportDirectory;

    public DailyReportServiceImpl(
            DailyReportRepository reportRepository,
            PolicyRenewalHistoryRepository renewalHistoryRepository,
            @Value("${report.directory:reports}") String reportDirectory) {
        this.reportRepository = reportRepository;
        this.renewalHistoryRepository = renewalHistoryRepository;
        this.reportDirectory = Path.of(reportDirectory);
    }

    @Override
    public void generateReport(LocalDate reportDate) {
        LocalDateTime start = reportDate.atStartOfDay();
        LocalDateTime end = reportDate.plusDays(1).atStartOfDay();

        Map<String, Long> policyCounts = new HashMap<>();
        for (Object[] row : reportRepository.countPoliciesByStatus()) {
            policyCounts.put(row[0].toString(), ((Number) row[1]).longValue());
        }
        policyCounts.put("RENEWED", renewalHistoryRepository.countRenewalsBetween(start, end));
        try {
            Files.createDirectories(reportDirectory);
            Path reportFile = reportDirectory.resolve(
                    "daily-report-" + reportDate + ".csv");

            try (BufferedWriter writer = Files.newBufferedWriter(reportFile)) {
                writer.write("report_date,section,tier,status,reason,count");
                writer.newLine();

                writePolicyRow(writer, reportDate, "ACTIVE", policyCounts);
                writePolicyRow(writer, reportDate, "RENEWAL_DUE", policyCounts);
                writePolicyRow(writer, reportDate, "RENEWED", policyCounts);
                writePolicyRow(writer, reportDate, "LAPSED", policyCounts);

                for (Object[] row :
                        reportRepository.countNotificationsByTierAndOutcome(start, end)) {
                    writeRow(writer, reportDate, "NOTIFICATION",
                            row[0], row[1], row[2], row[3]);
                }

                for (Object[] row :
                        reportRepository.countDeadLettersByTierAndReason(start, end)) {
                    writeRow(writer, reportDate, "NOTIFICATION",
                            row[0], "DEAD_LETTERED", row[1], row[2]);
                }

                writeFunnelRows(writer, reportDate, start, end);
            }
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Unable to write daily report for " + reportDate, ex);
        }
    }

    private void writeFunnelRows(
            BufferedWriter writer,
            LocalDate reportDate,
            LocalDateTime start,
            LocalDateTime end) throws IOException {
        Map<ReminderTier, Long> invalidContacts = countByTier(
                reportRepository.countInvalidContactsByTier(start, end));
        Map<ReminderTier, Long> deadLetters = countByTier(
                reportRepository.countDeadLettersByTier(start, end));

        Map<String, Long> policyFunnelCounts = new HashMap<>();
        for (Object[] row : reportRepository.countPoliciesForFunnel(reportDate)) {
            policyFunnelCounts.put(row[0].toString(), ((Number) row[1]).longValue());
        }
        writeRow(writer, reportDate, "FUNNEL", "",
                "ACTIVE", "UPDATED_DATE", policyFunnelCounts.getOrDefault("ACTIVE", 0L));
        writeRow(writer, reportDate, "FUNNEL", "",
                "RENEWED", "UPDATED_DATE", policyFunnelCounts.getOrDefault("RENEWED", 0L));

        for (ReminderTier tier : new ReminderTier[]{
                ReminderTier.THIRTY_DAY,
                ReminderTier.FIFTEEN_DAY,
                ReminderTier.SEVEN_DAY}) {
            writeRow(writer, reportDate, "FUNNEL", tier,
                    "INVALID_CONTACT", "NOTIFICATION_LOG",
                    invalidContacts.getOrDefault(tier, 0L));
            writeRow(writer, reportDate, "FUNNEL", tier,
                    "DEAD_LETTER", "REMINDER_DLQ",
                    deadLetters.getOrDefault(tier, 0L));
        }
    }

    private Map<ReminderTier, Long> countByTier(
            Iterable<Object[]> rows) {
        Map<ReminderTier, Long> counts = new EnumMap<>(ReminderTier.class);
        for (Object[] row : rows) {
            counts.put(ReminderTier.valueOf(row[0].toString()),
                    ((Number) row[1]).longValue());
        }
        return counts;
    }

    private void writePolicyRow(
            BufferedWriter writer,
            LocalDate reportDate,
            String status,
            Map<String, Long> policyCounts) throws IOException {
        writeRow(writer, reportDate, "POLICY", "", status, "", policyCounts.getOrDefault(status, 0L));
    }

    private void writeRow(
            BufferedWriter writer,
            LocalDate reportDate,
            String section,
            Object tier,
            Object status,
            Object reason,
            Object count) throws IOException {
        writer.write(String.join(",",
                csv(reportDate),
                csv(section),
                csv(tier),
                csv(status),
                csv(reason),
                csv(count)));
        writer.newLine();
    }

    private String csv(Object value) {
        String text = value == null ? "" : value.toString();
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}
