package com.fd.insurance.service;

import com.fd.insurance.dto.PolicyDetailsResponse;
import com.fd.insurance.dto.PolicyRenewRequest;
import com.fd.insurance.dto.ReminderHistoryResponse;
import com.fd.insurance.enums.ChannelType;
import com.fd.insurance.enums.NotificationOutcome;
import com.fd.insurance.enums.ReminderTier;
import com.fd.insurance.exception.DuplicateCustomerException;
import com.fd.insurance.exception.PolicyNotFoundException;
import com.fd.insurance.dto.PolicyRequest;
import com.fd.insurance.entity.Policy;
import com.fd.insurance.entity.PolicyRenewalHistory;
import com.fd.insurance.enums.PolicyStatus;
import com.fd.insurance.repository.NotificationLogRepository;
import com.fd.insurance.repository.PolicyRepository;
import com.fd.insurance.repository.PolicyRenewalHistoryRepository;
import com.fd.insurance.repository.ReminderOutboxRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository repository;
    private final Random random = new Random();
    private final ReminderOutboxRepository reminderOutboxRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final PolicyRenewalHistoryRepository renewalHistoryRepository;

    public PolicyServiceImpl(
            PolicyRepository repository,
            ReminderOutboxRepository reminderOutboxRepository,
            NotificationLogRepository notificationLogRepository,
            PolicyRenewalHistoryRepository renewalHistoryRepository) {
        this.repository = repository;
        this.reminderOutboxRepository = reminderOutboxRepository;
        this.notificationLogRepository = notificationLogRepository;
        this.renewalHistoryRepository = renewalHistoryRepository;
    }

    @Override
    public void createPolicy(PolicyRequest request) {

        if (!request.getStartDate().isBefore(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        String policyNumber = generatePolicyNumber();

        Optional<Policy> emailPolicy = repository.findByEmail(request.getEmail());

        if (emailPolicy.isPresent()) {
            Policy existing = emailPolicy.get();

            if (!existing.getHolderName().equalsIgnoreCase(request.getHolderName())) {
                throw new DuplicateCustomerException("Email already registered for another user.");
            }
        }

        Optional<Policy> phonePolicy = repository.findByPhone(request.getPhone());

        if (phonePolicy.isPresent()) {

            Policy existing = phonePolicy.get();

            if (!existing.getHolderName().equalsIgnoreCase(request.getHolderName())) {
                throw new DuplicateCustomerException("Phone number already registered for another user.");
            }
        }

        Policy policy = new Policy();

        policy.setPolicyNumber(policyNumber);
        policy.setHolderName(request.getHolderName());
        policy.setEmail(request.getEmail());
        policy.setPhone(request.getPhone());
        policy.setPolicyType(request.getPolicyType());
        policy.setPremiumAmount(request.getPremiumAmount());
        policy.setStartDate(request.getStartDate());
        policy.setEndDate(request.getEndDate());

        // System Controlled
        policy.setStatus(
                request.getEndDate().isBefore(LocalDate.now())
                        ? PolicyStatus.EXPIRED
                        : PolicyStatus.ACTIVE
        );

        repository.save(policy);
    }

    private String generatePolicyNumber() {
        String policyNumber;
        do {
            policyNumber = "POL" + (100000 + random.nextInt(900000));
        } while (repository.findByPolicyNumber(policyNumber).isPresent());
        return policyNumber;
    }

    @Override
    @Transactional
    public void renewPolicy(String policyNumber, PolicyRenewRequest request) {

        Policy policy = repository.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new PolicyNotFoundException("Policy not found"));

        if (policy.getStatus() != PolicyStatus.EXPIRED
                && policy.getStatus() != PolicyStatus.RENEWAL_DUE) {

            throw new IllegalArgumentException("Only EXPIRED or RENEWAL_DUE policies can be renewed.");
        }

        LocalDate today = LocalDate.now();
        LocalDate currentEndDate = policy.getEndDate();

        // 15-day grace period

        if (today.isAfter(currentEndDate.plusDays(15))) {
            throw new IllegalArgumentException("Renewal grace period has expired.");
        }

        if (request.getEndDate().isBefore(currentEndDate)) {
            throw new IllegalArgumentException("New end date must be on or after current policy end date.");
        }

        repository.updatePolicyForRenewal(policyNumber, request.getEndDate(), PolicyStatus.ACTIVE);
        PolicyRenewalHistory history = new PolicyRenewalHistory();
        history.setPolicyNumber(policyNumber);
        history.setRenewedAt(LocalDateTime.now());
        renewalHistoryRepository.save(history);
        reminderOutboxRepository.deleteByPolicyNumber(policyNumber);
    }

    @Override
    public PolicyDetailsResponse getPolicy(String policyNumber) {

        Policy policy =
                repository
                        .findByPolicyNumber(policyNumber)
                        .orElseThrow(() ->
                                new PolicyNotFoundException(
                                        "Policy not found: "
                                                + policyNumber));

        List<Object[]> historyRows = notificationLogRepository
                        .findHistoryByPolicyNumber(policyNumber);

        List<ReminderHistoryResponse> history = new ArrayList<>();

        for (Object[] row : historyRows) {

            ReminderHistoryResponse item = new ReminderHistoryResponse();

            item.setEventId(((Number) row[0]).longValue());

            item.setChannel(ChannelType.valueOf(row[1].toString()));

            item.setTier(ReminderTier.valueOf(row[2].toString()));

            item.setEventTime((LocalDateTime) row[3]);

            item.setOutcome(NotificationOutcome.valueOf(row[4].toString()));

            history.add(item);
        }

        PolicyDetailsResponse response = new PolicyDetailsResponse();

        response.setPolicyNumber(policy.getPolicyNumber());
        response.setHolderName(policy.getHolderName());
        response.setEmail(policy.getEmail());
        response.setPhone(policy.getPhone());
        response.setPolicyType(policy.getPolicyType());
        response.setPremiumAmount(policy.getPremiumAmount());
        response.setStartDate(policy.getStartDate());
        response.setEndDate(policy.getEndDate());
        response.setStatus(policy.getStatus());
        response.setReminderHistory(history);

        return response;
    }
}