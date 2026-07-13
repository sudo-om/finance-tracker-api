package com.financetracker.finance_tracker_api.service;

import com.financetracker.finance_tracker_api.dto.request.ExpenseCreateRequest;
import com.financetracker.finance_tracker_api.dto.response.ExpenseResponse;

public interface ExpenseService {

    ExpenseResponse createExpense(
            ExpenseCreateRequest request
    );
}