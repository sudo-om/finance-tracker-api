package com.financetracker.finance_tracker_api.service;

import com.financetracker.finance_tracker_api.dto.request.IncomeCreateRequest;
import com.financetracker.finance_tracker_api.dto.request.IncomeFilterRequest;
import com.financetracker.finance_tracker_api.dto.request.IncomeUpdateRequest;
import com.financetracker.finance_tracker_api.dto.response.IncomeResponse;
import com.financetracker.finance_tracker_api.dto.response.PagedResponse;
import com.financetracker.finance_tracker_api.entity.User;


import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IncomeService {

    IncomeResponse createIncome(IncomeCreateRequest request);

    IncomeResponse createIncome(IncomeCreateRequest request, User user);

    PagedResponse<IncomeResponse> getAllIncomes(IncomeFilterRequest request, Pageable pageable);

    IncomeResponse getIncomeById(UUID id);

    IncomeResponse updateIncome(UUID id, IncomeUpdateRequest request);

    void deleteIncome(UUID id);
}
