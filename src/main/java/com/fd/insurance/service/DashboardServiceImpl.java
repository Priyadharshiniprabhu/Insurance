package com.fd.insurance.service;

import com.fd.insurance.dto.DashboardSummaryResponse;
import com.fd.insurance.entity.ReminderDLQ;
import com.fd.insurance.repository.NotificationLogRepository;
import com.fd.insurance.repository.PolicyRenewalHistoryRepository;
import com.fd.insurance.repository.PolicyRepository;
import com.fd.insurance.repository.ReminderDLQRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
        Map<String, DashboardSummaryResponse.LatestPolicyOutcome> latestByPolicy =
                new LinkedHashMap<>();

        for (Object[] row : notificationLogRepository.findLatestOutcomesByPolicy()) {
            String policyNumber = (String) row[0];
            String status = row[1].toString();
            String reason = row[2] == null ? null : row[2].toString();
            LocalDateTime eventTime = (LocalDateTime) row[3];
            latestByPolicy.put(policyNumber,
                    new DashboardSummaryResponse.LatestPolicyOutcome(
                            policyNumber, status, reason, eventTime, false));
        }

        for (ReminderDLQ dlq : reminderDLQRepository.findAll()) {
            String policyNumber = dlq.getPolicyNumber();
            if (policyNumber == null) {
                continue;
            }
            DashboardSummaryResponse.LatestPolicyOutcome current =
                    latestByPolicy.get(policyNumber);
            if (current == null || !current.isInDeadLetterQueue()
                    || isAfter(dlq.getMovedAt(), current.getEventTime())) {
                latestByPolicy.put(policyNumber,
                        new DashboardSummaryResponse.LatestPolicyOutcome(
                                policyNumber,
                                "DEAD_LETTER",
                                dlq.getLastError() == null ? null : dlq.getLastError().name(),
                                dlq.getMovedAt(),
                                true));
            }
        }

        long sent = latestByPolicy.values().stream()
                .filter(outcome -> !outcome.isInDeadLetterQueue())
                .filter(outcome -> "SENT".equals(outcome.getStatus()))
                .count();
        long invalidContact = latestByPolicy.values().stream()
                .filter(outcome -> !outcome.isInDeadLetterQueue())
                .filter(outcome -> "INVALID_CONTACT".equals(outcome.getReason()))
                .count();
        long deadLetters = latestByPolicy.values().stream()
                .filter(DashboardSummaryResponse.LatestPolicyOutcome::isInDeadLetterQueue)
                .count();
        long failed = latestByPolicy.size() - sent - invalidContact - deadLetters;

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
                        deadLetters,
                        new ArrayList<>(latestByPolicy.values())));
    }

    private boolean isAfter(LocalDateTime candidate, LocalDateTime current) {
        if (candidate == null) {
            return false;
        }
        return current == null || candidate.isAfter(current);
    }

}
