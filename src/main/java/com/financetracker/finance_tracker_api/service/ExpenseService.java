package com.financetracker.finance_tracker_api.service;

import com.financetracker.finance_tracker_api.dto.request.ExpenseCreateRequest;
import com.financetracker.finance_tracker_api.dto.request.ExpenseFilterRequest;
import com.financetracker.finance_tracker_api.dto.request.ExpenseUpdateRequest;
import com.financetracker.finance_tracker_api.dto.response.ExpenseResponse;
import com.financetracker.finance_tracker_api.dto.response.PagedResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ExpenseService {

    ExpenseResponse createExpense(
            ExpenseCreateRequest request
    );

    PagedResponse<ExpenseResponse> getAllExpenses(
            ExpenseFilterRequest request,
            Pageable pageable
    );

    ExpenseResponse getExpenseById(UUID id);

    ExpenseResponse updateExpense(
            UUID id,
            ExpenseUpdateRequest request
    );

    void deleteExpense(UUID id);
}