package com.financetracker.finance_tracker_api.controller;

import com.financetracker.finance_tracker_api.dto.request.ExpenseCreateRequest;
import com.financetracker.finance_tracker_api.dto.request.ExpenseFilterRequest;
import com.financetracker.finance_tracker_api.dto.request.ExpenseUpdateRequest;
import com.financetracker.finance_tracker_api.dto.response.ApiResponse;
import com.financetracker.finance_tracker_api.dto.response.ExpenseResponse;
import com.financetracker.finance_tracker_api.dto.response.PagedResponse;
import com.financetracker.finance_tracker_api.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(
            @Valid @RequestBody ExpenseCreateRequest request) {

        ExpenseResponse response = expenseService.createExpense(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ExpenseResponse>>> getAllExpenses(

            ExpenseFilterRequest request,
            Pageable pageable
    ) {

        PagedResponse<ExpenseResponse> response =
                expenseService.getAllExpenses(request, pageable);

        ApiResponse<PagedResponse<ExpenseResponse>> apiResponse =
                ApiResponse.<PagedResponse<ExpenseResponse>>builder()
                        .success(true)
                        .message("Expenses fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>>
    getExpenseById(
            @PathVariable UUID id
    ) {

        ExpenseResponse response =
                expenseService.getExpenseById(id);

        ApiResponse<ExpenseResponse> apiResponse =
                ApiResponse.<ExpenseResponse>builder()
                        .success(true)
                        .message("Expense fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(apiResponse);

    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(

            @PathVariable UUID id,

            @Valid
            @RequestBody ExpenseUpdateRequest request
    ) {

        ExpenseResponse response =
                expenseService.updateExpense(id, request);

        ApiResponse<ExpenseResponse> apiResponse =
                ApiResponse.<ExpenseResponse>builder()
                        .success(true)
                        .message("Expense updated successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @PathVariable UUID id
    ) {

        expenseService.deleteExpense(id);

        ApiResponse<Void> response =
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Expense deleted successfully")
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }

}
