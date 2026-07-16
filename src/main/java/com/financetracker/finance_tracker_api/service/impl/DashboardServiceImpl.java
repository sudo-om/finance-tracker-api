package com.financetracker.finance_tracker_api.service.impl;

import com.financetracker.finance_tracker_api.dto.response.*;
import com.financetracker.finance_tracker_api.entity.User;
import com.financetracker.finance_tracker_api.mapper.ExpenseMapper;
import com.financetracker.finance_tracker_api.projection.DashboardSummaryProjection;
import com.financetracker.finance_tracker_api.repository.ExpenseRepository;
import com.financetracker.finance_tracker_api.repository.IncomeRepository;
import com.financetracker.finance_tracker_api.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ExpenseMapper expenseMapper;
    private final CurrentUserServiceImpl currentUserService;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {

        User currentUser = currentUserService.getCurrentUser();

        DashboardSummaryProjection projection =
                expenseRepository.getDashboardSummary(currentUser);

        return DashboardSummaryResponse.builder()
                .totalExpense(projection.getTotalExpense())
                .totalTransactions(projection.getTotalTransactions())
                .averageExpense(projection.getAverageExpense())
                .highestExpense(projection.getHighestExpense())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyExpenseResponse> getMonthlyExpenses() {

        User currentUser =
                currentUserService.getCurrentUser();

        return expenseRepository
                .getMonthlyExpenses(currentUser)
                .stream()
                .map(projection ->

                        MonthlyExpenseResponse.builder()
                                .month(projection.getMonth())
                                .total(projection.getTotal())
                                .build()

                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySpendingResponse> getCategorySpending() {

        User currentUser =
                currentUserService.getCurrentUser();

        return expenseRepository
                .getCategorySpending(currentUser)
                .stream()
                .map(projection ->

                        CategorySpendingResponse.builder()
                                .category(projection.getCategory())
                                .total(projection.getTotal())
                                .build()

                )
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getRecentTransactions() {

        User currentUser = currentUserService.getCurrentUser();

        return expenseRepository
                .findTop5ByUserOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(expenseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceResponse getBalance() {

        User currentUser = currentUserService.getCurrentUser();

        BigDecimal totalIncome =
                incomeRepository.getTotalIncome(currentUser);

        BigDecimal totalExpense =
                expenseRepository.getTotalExpense(currentUser);

        BigDecimal currentBalance =
                totalIncome.subtract(totalExpense);

        BigDecimal savingsRate;

        if (totalIncome.compareTo(BigDecimal.ZERO) == 0) {
            savingsRate = BigDecimal.ZERO;
        } else {
            savingsRate = currentBalance
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalIncome, 2, RoundingMode.HALF_UP);
        }

        return BalanceResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .currentBalance(currentBalance)
                .savingsRate(savingsRate)
                .build();
    }
}
