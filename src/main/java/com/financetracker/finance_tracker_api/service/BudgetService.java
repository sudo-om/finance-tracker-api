package com.financetracker.finance_tracker_api.service;

import com.financetracker.finance_tracker_api.dto.request.BudgetCreateRequest;
import com.financetracker.finance_tracker_api.dto.response.BudgetResponse;
import com.financetracker.finance_tracker_api.dto.response.PagedResponse;
import com.financetracker.finance_tracker_api.dto.request.BudgetUpdateRequest;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BudgetService {

    BudgetResponse createBudget(BudgetCreateRequest request);

    BudgetResponse getBudgetById(UUID id);

    PagedResponse<BudgetResponse> getAllBudgets(
            Pageable pageable
    );

    BudgetResponse updateBudget(
            UUID id,
            BudgetUpdateRequest request
    );

    void deleteBudget(UUID id);
}