package com.financetracker.finance_tracker_api.service;

import com.financetracker.finance_tracker_api.dto.response.*;
import com.financetracker.finance_tracker_api.entity.Expense;
import com.financetracker.finance_tracker_api.entity.User;

import java.util.List;

public interface DashboardService {
    DashboardSummaryResponse getSummary();

    DashboardSummaryResponse getSummary(User user);

    List<MonthlyExpenseResponse> getMonthlyExpenses();

    List<CategorySpendingResponse> getCategorySpending();

    List<ExpenseResponse> getRecentTransactions();

    BalanceResponse getBalance();

    BalanceResponse getBalance(User user);


}
