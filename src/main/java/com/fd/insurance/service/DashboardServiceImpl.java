package com.fd.insurance.service;

import com.fd.insurance.dto.DashboardSummaryResponse;
import com.fd.insurance.repository.NotificationLogRepository;
import com.fd.insurance.repository.PolicyRenewalHistoryRepository;
import com.fd.insurance.repository.PolicyRepository;
import com.fd.insurance.repository.ReminderDLQRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final PolicyRepository policyRepository;
    private final PolicyRenewalHistoryRepository renewalHistoryRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final ReminderDLQRepository reminderDLQRepository;

    public DashboardServiceImpl(
            PolicyRepository policyRepository,
            PolicyRenewalHistoryRepository renewalHistoryRepository,
            NotificationLogRepository notificationLogRepository,
            ReminderDLQRepository reminderDLQRepository) {
        this.policyRepository = policyRepository;
        this.renewalHistoryRepository = renewalHistoryRepository;
        this.notificationLogRepository = notificationLogRepository;
        this.reminderDLQRepository = reminderDLQRepository;
    }

    @Override
    public DashboardSummaryResponse getSummary() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime startOfTomorrow = today.plusDays(1).atStartOfDay();
        Map<String, Long> outcomeCounts = new HashMap<>();
        for (Object[] row : notificationLogRepository.countOutcomesBetween(
                startOfToday, startOfTomorrow)) {
            String status = row[0].toString();
            String reason = row[1] == null ? "" : row[1].toString();
            long count = ((Number) row[2]).longValue();
            outcomeCounts.merge(status + ":" + reason, count, Long::sum);
        }
        long invalidContact = outcomeCounts.getOrDefault(
                "FAILED:INVALID_CONTACT", 0L);
        long failed = outcomeCounts.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("FAILED:"))
                .filter(entry -> !entry.getKey().equals("FAILED:INVALID_CONTACT"))
                .mapToLong(Map.Entry::getValue)
                .sum();
        long sent = outcomeCounts.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("SENT:"))
                .mapToLong(Map.Entry::getValue)
                .sum();
        return new DashboardSummaryResponse(
                policyRepository.countAll(),
                policyRepository.countActiveExcludingUpdatedDate(today),
                policyRepository.countActiveUpdatedOn(today),
                policyRepository.countOverdue(),
                new DashboardSummaryResponse.FunnelSummary(
                        policyRepository.countRenewalCandidatesDueBetween(
                                today.plusDays(16), today.plusDays(30)),
                        policyRepository.countRenewalCandidatesDueBetween(
                                today.plusDays(8), today.plusDays(15)),
                        policyRepository.countRenewalCandidatesDueBetween(
                                today, today.plusDays(7)),
                        renewalHistoryRepository.countRenewalsBetween(
                                startOfToday, startOfTomorrow)),
                new DashboardSummaryResponse.NotificationSummary(
                        sent,
                        failed,
                        invalidContact,
                        reminderDLQRepository.countAll()));
    }
}
