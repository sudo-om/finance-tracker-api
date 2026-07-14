package com.financetracker.finance_tracker_api.service.impl;

import com.financetracker.finance_tracker_api.dto.response.CategorySpendingResponse;
import com.financetracker.finance_tracker_api.dto.response.DashboardSummaryResponse;
import com.financetracker.finance_tracker_api.dto.response.ExpenseResponse;
import com.financetracker.finance_tracker_api.dto.response.MonthlyExpenseResponse;
import com.financetracker.finance_tracker_api.entity.Expense;
import com.financetracker.finance_tracker_api.entity.User;
import com.financetracker.finance_tracker_api.mapper.ExpenseMapper;
import com.financetracker.finance_tracker_api.projection.DashboardSummaryProjection;
import com.financetracker.finance_tracker_api.repository.ExpenseRepository;
import com.financetracker.finance_tracker_api.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ExpenseMapper expenseMapper;
    private final CurrentUserServiceImpl currentUserService;
    private final ExpenseRepository expenseRepository;

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
}
