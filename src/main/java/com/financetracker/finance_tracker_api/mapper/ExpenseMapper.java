package com.financetracker.finance_tracker_api.mapper;

import com.financetracker.finance_tracker_api.dto.response.ExpenseResponse;
import com.financetracker.finance_tracker_api.entity.Expense;

public interface ExpenseMapper {
    ExpenseResponse toResponse(Expense savedExpense);
}
