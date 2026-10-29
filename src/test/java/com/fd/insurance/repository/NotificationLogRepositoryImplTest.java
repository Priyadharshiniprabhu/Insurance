package com.fd.insurance.repository;

import com.fd.insurance.entity.NotificationLog;
import com.fd.insurance.enums.NotificationReason;
import com.fd.insurance.enums.NotificationStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:notification-log-repository-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.batch.job.enabled=false",
        "spring.main.web-application-type=none"
})
@Transactional
class NotificationLogRepositoryImplTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    @Test
    void returnsOnlyMostRecentLogPerPolicyAndBreaksTimestampTiesById() {
        LocalDateTime older = LocalDateTime.of(2026, 10, 28, 12, 0);
        LocalDateTime latest = older.plusHours(1);
        save("POL001", NotificationStatus.SENT, NotificationReason.OK, older);
        save("POL001", NotificationStatus.FAILED, NotificationReason.GATEWAY_TIMEOUT, latest);
        save("POL001", NotificationStatus.FAILED, NotificationReason.INVALID_CONTACT, latest);
        save("POL002", NotificationStatus.SENT, NotificationReason.OK, older);
        entityManager.flush();
        entityManager.clear();

        List<Object[]> rows = notificationLogRepository.findLatestOutcomesByPolicy();

        assertEquals(2, rows.size());
        assertEquals("POL001", rows.get(0)[0]);
        assertEquals("FAILED", rows.get(0)[1].toString());
        assertEquals("INVALID_CONTACT", rows.get(0)[2].toString());
        assertEquals(latest, rows.get(0)[3]);
        assertEquals("POL002", rows.get(1)[0]);
        assertEquals("SENT", rows.get(1)[1].toString());
    }

    private void save(
            String policyNumber,
            NotificationStatus status,
            NotificationReason reason,
            LocalDateTime eventTime) {
        NotificationLog log = new NotificationLog();
        log.setPolicyNumber(policyNumber);
        log.setStatus(status);
        log.setReason(reason);
        log.setEventTime(eventTime);
        entityManager.persist(log);
    }
}
