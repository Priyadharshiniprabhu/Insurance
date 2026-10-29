package com.fd.insurance.service;

import com.fd.insurance.dto.DashboardSummaryResponse;
import com.fd.insurance.entity.ReminderDLQ;
import com.fd.insurance.enums.NotificationReason;
import com.fd.insurance.repository.NotificationLogRepository;
import com.fd.insurance.repository.PolicyRenewalHistoryRepository;
import com.fd.insurance.repository.PolicyRepository;
import com.fd.insurance.repository.ReminderDLQRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private PolicyRepository policyRepository;
    @Mock
    private PolicyRenewalHistoryRepository renewalHistoryRepository;
    @Mock
    private NotificationLogRepository notificationLogRepository;
    @Mock
    private ReminderDLQRepository reminderDLQRepository;
    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    void summaryCountsOneLatestOutcomePerPolicyAndLetsActiveDlqWin() {
        LocalDateTime eventTime = LocalDateTime.of(2026, 10, 28, 12, 0);
        when(notificationLogRepository.findLatestOutcomesByPolicy()).thenReturn(List.of(
                row("POL001", "SENT", "OK", eventTime),
                row("POL002", "SENT", "OK", eventTime),
                row("POL003", "SENT", "OK", eventTime),
                row("POL004", "FAILED", "INTERNAL_SERVER_ERROR", eventTime),
                row("POL005", "FAILED", "GATEWAY_TIMEOUT", eventTime),
                row("POL006", "FAILED", "INTERNAL_SERVER_ERROR", eventTime),
                row("POL007", "FAILED", "INVALID_CONTACT", eventTime),
                row("POL008", "FAILED", "GATEWAY_TIMEOUT", eventTime),
                row("POL009", "FAILED", "INTERNAL_SERVER_ERROR", eventTime),
                row("POL010", "FAILED", "GATEWAY_TIMEOUT", eventTime),
                row("POL011", "FAILED", "GATEWAY_TIMEOUT", eventTime)));
        when(reminderDLQRepository.findAll()).thenReturn(List.of(
                dlq("POL008", eventTime.plusMinutes(4)),
                dlq("POL008", eventTime.plusMinutes(3)),
                dlq("POL009", eventTime.plusMinutes(4)),
                dlq("POL010", eventTime.plusMinutes(4)),
                dlq("POL011", eventTime.plusMinutes(4))));
        when(policyRepository.countAll()).thenReturn(11L);
        when(policyRepository.countActiveExcludingUpdatedDate(any(LocalDate.class))).thenReturn(0L);
        when(policyRepository.countActiveUpdatedOn(any(LocalDate.class))).thenReturn(0L);
        when(policyRepository.countOverdue()).thenReturn(0L);
        when(policyRepository.countRenewalCandidatesDueBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(0L);
        when(renewalHistoryRepository.countRenewalsBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(0L);

        DashboardSummaryResponse.NotificationSummary summary =
                dashboardService.getSummary().getNotifications();

        assertEquals(3L, summary.getSent());
        assertEquals(3L, summary.getFailed());
        assertEquals(1L, summary.getInvalidContact());
        assertEquals(4L, summary.getDeadLetters());
        assertEquals(11, summary.getLatestByPolicy().size());
        assertEquals(11L, summary.getSent() + summary.getFailed()
                + summary.getInvalidContact() + summary.getDeadLetters());

        Map<String, DashboardSummaryResponse.LatestPolicyOutcome> byPolicy =
                summary.getLatestByPolicy().stream().collect(Collectors.toMap(
                        DashboardSummaryResponse.LatestPolicyOutcome::getPolicyNumber,
                        Function.identity()));
        assertEquals("DEAD_LETTER", byPolicy.get("POL008").getStatus());
        assertEquals(eventTime.plusMinutes(4), byPolicy.get("POL008").getEventTime());
        assertEquals("FAILED", byPolicy.get("POL007").getStatus());
        assertEquals("INVALID_CONTACT", byPolicy.get("POL007").getReason());
    }

    private Object[] row(String policyNumber, String status, String reason, LocalDateTime eventTime) {
        return new Object[]{policyNumber, status, reason, eventTime};
    }

    private ReminderDLQ dlq(String policyNumber, LocalDateTime movedAt) {
        ReminderDLQ dlq = new ReminderDLQ();
        dlq.setPolicyNumber(policyNumber);
        dlq.setMovedAt(movedAt);
        dlq.setLastError(NotificationReason.GATEWAY_TIMEOUT);
        return dlq;
    }
}
