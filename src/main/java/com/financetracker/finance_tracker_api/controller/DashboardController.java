package com.financetracker.finance_tracker_api.controller;

import com.financetracker.finance_tracker_api.dto.response.*;
import com.financetracker.finance_tracker_api.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;


    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary() {

        DashboardSummaryResponse response =
                dashboardService.getSummary();

        return ResponseEntity.ok(
                ApiResponse.<DashboardSummaryResponse>builder()
                        .success(true)
                        .message("Dashboard summary fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/monthly-expenses")
    public ResponseEntity<ApiResponse<List<MonthlyExpenseResponse>>>
    getMonthlyExpenses() {

        List<MonthlyExpenseResponse> response =
                dashboardService.getMonthlyExpenses();

        return ResponseEntity.ok(

                ApiResponse.<List<MonthlyExpenseResponse>>builder()
                        .success(true)
                        .message("Monthly expense trend fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()

        );
    }

    @GetMapping("/category-spending")
    public ResponseEntity<ApiResponse<List<CategorySpendingResponse>>>
    getCategorySpending() {

        List<CategorySpendingResponse> response =
                dashboardService.getCategorySpending();

        return ResponseEntity.ok(

                ApiResponse.<List<CategorySpendingResponse>>builder()
                        .success(true)
                        .message("Category spending fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()

        );

    }

    @GetMapping("/recent-transactions")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getRecentTransactions() {

        List<ExpenseResponse> response =
                dashboardService.getRecentTransactions();

        ApiResponse<List<ExpenseResponse>> apiResponse =
                ApiResponse.<List<ExpenseResponse>>builder()
                        .success(true)
                        .message("Recent transactions fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<BalanceResponse>> getBalance() {

        BalanceResponse response = dashboardService.getBalance();

        ApiResponse<BalanceResponse> apiResponse =
                ApiResponse.<BalanceResponse>builder()
                        .success(true)
                        .message("Balance fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(apiResponse);

    }
}
