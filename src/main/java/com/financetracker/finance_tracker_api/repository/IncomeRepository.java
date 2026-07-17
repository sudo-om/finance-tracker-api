package com.financetracker.finance_tracker_api.repository;

import com.financetracker.finance_tracker_api.entity.Income;
import com.financetracker.finance_tracker_api.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IncomeRepository extends JpaRepository<Income, UUID>, JpaSpecificationExecutor<Income> {

    List<Income> findByUserId(UUID userId);

    Optional<Income> findByIdAndUser(
            UUID id,
            User user
    );

    @Query("""
    SELECT COALESCE(SUM(i.amount), 0)
    FROM Income i
    WHERE i.user = :user
    """)
    BigDecimal getTotalIncome(@Param("user") User user);

    @Query("""
    SELECT COALESCE(SUM(i.amount),0)
    FROM Income i
    WHERE i.user=:user
    AND i.incomeDate BETWEEN :startDate AND :endDate
    """)
    BigDecimal getTotalIncome(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
    SELECT i.source
    FROM Income i
    WHERE i.user=:user
    AND i.incomeDate BETWEEN :startDate AND :endDate
    GROUP BY i.source
    ORDER BY SUM(i.amount) DESC
    """)
    List<String> findTopIncomeSources(
            User user,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    @Query("""
    SELECT COALESCE(MAX(i.amount),0)
    FROM Income i
    WHERE i.user = :user
    AND i.incomeDate BETWEEN :startDate AND :endDate
    """)
    BigDecimal getLargestIncome(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );



}
