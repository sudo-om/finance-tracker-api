package com.financetracker.finance_tracker_api.service;

import com.financetracker.finance_tracker_api.dto.response.*;
import com.financetracker.finance_tracker_api.entity.Expense;

import java.util.List;

public interface DashboardService {
    DashboardSummaryResponse getSummary();

    List<MonthlyExpenseResponse> getMonthlyExpenses();

    List<CategorySpendingResponse> getCategorySpending();

    List<ExpenseResponse> getRecentTransactions();

    BalanceResponse getBalance();
}
