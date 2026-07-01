package com.financetracker.finance_tracker_api.repository;

import com.financetracker.finance_tracker_api.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    List<Expense> findByUserId(UUID userId);

    List<Expense> findByExpenseDateBetween(LocalDate start, LocalDate end);

}
