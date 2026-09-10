package com.fd.insurance.repository;

import com.fd.insurance.entity.Policy;
import com.fd.insurance.enums.PolicyStatus;
import com.fd.insurance.enums.PolicyType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class PolicyRepositoryImpl implements PolicyRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Policy policy) {

        entityManager.createNativeQuery(
                        """
                        INSERT INTO policies (
                            policy_number,
                            holder_name,
                            email,
                            phone,
                            policy_type,
                            premium_amount,
                            start_date,
                            end_date,
                            status
                        )
                        VALUES (
                            :policyNumber,
                            :holderName,
                            :email,
                            :phone,
                            :policyType,
                            :premiumAmount,
                            :startDate,
                            :endDate,
                            :status
                        )
                        """
                )
                .setParameter("policyNumber", policy.getPolicyNumber())
                .setParameter("holderName", policy.getHolderName())
                .setParameter("email", policy.getEmail())
                .setParameter("phone", policy.getPhone())
                .setParameter("policyType", policy.getPolicyType().name())
                .setParameter("premiumAmount", policy.getPremiumAmount())
                .setParameter("startDate", policy.getStartDate())
                .setParameter("endDate", policy.getEndDate())
                .setParameter("status", policy.getStatus().name())
                .executeUpdate();
    }

    @Override
    public Optional<Policy> findByPolicyNumber(String policyNumber) {

        List<Object[]> result = entityManager
                .createNativeQuery(
                        """
                        SELECT
                            id,
                            policy_number,
                            holder_name,
                            email,
                            phone,
                            policy_type,
                            premium_amount,
                            start_date,
                            end_date,
                            status
                        FROM policies
                        WHERE policy_number = :policyNumber
                        """
                )
                .setParameter("policyNumber", policyNumber)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        Object[] row = result.get(0);

        Policy policy = new Policy();

        policy.setId(((Number) row[0]).longValue());
        policy.setPolicyNumber((String) row[1]);
        policy.setHolderName((String) row[2]);
        policy.setEmail((String) row[3]);
        policy.setPhone((String) row[4]);

        policy.setPolicyType(PolicyType.valueOf((String) row[5]));

        policy.setPremiumAmount((java.math.BigDecimal) row[6]);
        policy.setStartDate((LocalDate) row[7]);
        policy.setEndDate((LocalDate) row[8]);
        policy.setStatus(PolicyStatus.valueOf((String) row[9]));

        return Optional.of(policy);
    }

    @Transactional
    public void updatePolicyForRenewal(String policyNumber, LocalDate endDate, PolicyStatus status) {

        entityManager.createNativeQuery("""
            UPDATE policies
            SET end_date = :endDate,
                status = :status
            WHERE policy_number = :policyNumber
            """)
                .setParameter("endDate", endDate)
                .setParameter("status", status.name())
                .setParameter("policyNumber", policyNumber)
                .executeUpdate();
    }

    @Override
    public List<Policy> findPoliciesForReminderDetection() {

        return entityManager.createQuery("""
            SELECT p
            FROM Policy p
            WHERE p.status IN (
                :active,
                :expired
            )
            """,
                        Policy.class)
                .setParameter("active", PolicyStatus.ACTIVE)
                .setParameter("expired", PolicyStatus.EXPIRED)
                .getResultList();
    }

    @Override
    public void updatePolicyStatus(String policyNumber, PolicyStatus status) {

        entityManager.createNativeQuery("""
            UPDATE POLICIES
            SET STATUS = :status
            WHERE POLICY_NUMBER = :policyNumber
            """)
                .setParameter("status", status.name())
                .setParameter("policyNumber", policyNumber)
                .executeUpdate();
    }

    @Override
    public Optional<Policy> findByEmail(String email) {

        List<Policy> policies =
                entityManager.createQuery("""
                    SELECT p
                    FROM Policy p
                    WHERE p.email = :email
                    """, Policy.class)
                        .setParameter("email", email)
                        .getResultList();

        return policies.stream().findFirst();
    }

    @Override
    public Optional<Policy> findByPhone(String phone) {

        List<Policy> policies =
                entityManager.createQuery("""
                    SELECT p
                    FROM Policy p
                    WHERE p.phone = :phone
                    """, Policy.class)
                        .setParameter("phone", phone)
                        .getResultList();

        return policies.stream().findFirst();
    }

    @Override
    public List<Policy> findExpiredPoliciesForLapse() {

        return entityManager
                .createQuery("""
                    SELECT p
                    FROM Policy p
                    WHERE p.status = :status
                    """,
                        Policy.class)
                .setParameter(
                        "status",
                        PolicyStatus.EXPIRED)
                .getResultList();
    }
}