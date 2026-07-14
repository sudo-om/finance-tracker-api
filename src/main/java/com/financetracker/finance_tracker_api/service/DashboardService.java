package com.financetracker.finance_tracker_api.service;

import com.financetracker.finance_tracker_api.dto.response.CategorySpendingResponse;
import com.financetracker.finance_tracker_api.dto.response.DashboardSummaryResponse;
import com.financetracker.finance_tracker_api.dto.response.ExpenseResponse;
import com.financetracker.finance_tracker_api.dto.response.MonthlyExpenseResponse;
import com.financetracker.finance_tracker_api.entity.Expense;

import java.util.List;

public interface DashboardService {
    DashboardSummaryResponse getSummary();

    List<MonthlyExpenseResponse> getMonthlyExpenses();

    List<CategorySpendingResponse> getCategorySpending();

    List<ExpenseResponse> getRecentTransactions();

}
