package com.financetracker.finance_tracker_api.repository;

import com.financetracker.finance_tracker_api.entity.Expense;
import com.financetracker.finance_tracker_api.entity.User;
import com.financetracker.finance_tracker_api.projection.CategorySpendingProjection;
import com.financetracker.finance_tracker_api.projection.DashboardSummaryProjection;
import com.financetracker.finance_tracker_api.projection.MonthlyExpenseProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID>, JpaSpecificationExecutor<Expense> {

    List<Expense> findByUserId(UUID userId);

    List<Expense> findByExpenseDateBetween(LocalDate start, LocalDate end);

    Page<Expense> findAllByUser(User user, Pageable pageable);

    Optional<Expense> findByIdAndUser(
            UUID id,
            User user
    );

    @Query("""
    SELECT
    COALESCE(SUM(e.amount), 0) AS totalExpense,
    COUNT(e) AS totalTransactions,
    COALESCE(AVG(e.amount), 0) AS averageExpense,
    COALESCE(MAX(e.amount), 0) AS highestExpense
    FROM Expense e
    WHERE e.user = :user
    """)
    DashboardSummaryProjection getDashboardSummary(User user);

    @Query("""
    SELECT
    MONTH(e.expenseDate) AS month,
    SUM(e.amount) AS total
    FROM Expense e
    WHERE e.user = :user
    GROUP BY MONTH(e.expenseDate)
    ORDER BY MONTH(e.expenseDate)
    """)
    List<MonthlyExpenseProjection> getMonthlyExpenses(User user);

    @Query("""
    SELECT
    e.category.name AS category,
    COALESCE(SUM(e.amount),0) AS total
    FROM Expense e
    WHERE e.user = :user
    GROUP BY e.category.name
    ORDER BY SUM(e.amount) DESC
    """)
    List<CategorySpendingProjection> getCategorySpending(User user);

    List<Expense> findTop5ByUserOrderByExpenseDateDesc(User user);

    List<Expense> findTop5ByUserOrderByCreatedAtDesc(User user);


    @Query("""
    SELECT COALESCE(SUM(e.amount), 0)
    FROM Expense e
    WHERE e.user = :user
    """)
    BigDecimal getTotalExpense(@Param("user") User user);
}
