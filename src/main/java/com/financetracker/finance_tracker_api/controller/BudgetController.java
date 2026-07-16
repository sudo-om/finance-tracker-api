package com.financetracker.finance_tracker_api.controller;

import com.financetracker.finance_tracker_api.dto.request.BudgetCreateRequest;
import com.financetracker.finance_tracker_api.dto.request.BudgetUpdateRequest;
import com.financetracker.finance_tracker_api.dto.response.ApiResponse;
import com.financetracker.finance_tracker_api.dto.response.BudgetResponse;
import com.financetracker.finance_tracker_api.dto.response.PagedResponse;
import com.financetracker.finance_tracker_api.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<ApiResponse<BudgetResponse>> createBudget(

            @Valid
            @RequestBody
            BudgetCreateRequest request
    ) {

        BudgetResponse response =
                budgetService.createBudget(request);

        ApiResponse<BudgetResponse> apiResponse =
                ApiResponse.<BudgetResponse>builder()
                        .success(true)
                        .message("Budget created successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetResponse>> getBudget(
            @PathVariable UUID id
    ){
        BudgetResponse response =
                budgetService.getBudgetById(id);

        ApiResponse<BudgetResponse> apiResponse =
                ApiResponse.<BudgetResponse>builder()
                        .success(true)
                        .message("Budget fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<BudgetResponse>>> getAllBudgets(
            Pageable pageable
    ) {

        PagedResponse<BudgetResponse> response =
                budgetService.getAllBudgets(
                        pageable
                );

        ApiResponse<PagedResponse<BudgetResponse>> apiResponse =
                ApiResponse.<PagedResponse<BudgetResponse>>builder()
                        .success(true)
                        .message("Budgets fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetResponse>>
    updateBudget(

            @PathVariable UUID id,

            @Valid
            @RequestBody
            BudgetUpdateRequest request
    ) {

        BudgetResponse response =
                budgetService.updateBudget(
                        id,
                        request
                );

        return ResponseEntity.ok(

                ApiResponse.<BudgetResponse>builder()
                        .success(true)
                        .message("Budget updated successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()

        );

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>>
    deleteBudget(
            @PathVariable UUID id
    ) {

        budgetService.deleteBudget(id);

        return ResponseEntity.ok(

                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Budget deleted successfully")
                        .timestamp(LocalDateTime.now())
                        .build()

        );

    }
}