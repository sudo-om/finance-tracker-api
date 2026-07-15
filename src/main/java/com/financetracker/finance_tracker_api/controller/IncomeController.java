package com.financetracker.finance_tracker_api.controller;

import com.financetracker.finance_tracker_api.dto.request.IncomeCreateRequest;
import com.financetracker.finance_tracker_api.dto.request.IncomeFilterRequest;
import com.financetracker.finance_tracker_api.dto.request.IncomeUpdateRequest;
import com.financetracker.finance_tracker_api.dto.response.ApiResponse;
import com.financetracker.finance_tracker_api.dto.response.IncomeResponse;
import com.financetracker.finance_tracker_api.dto.response.PagedResponse;
import com.financetracker.finance_tracker_api.service.IncomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<ApiResponse<IncomeResponse>> createIncome(

            @Valid
            @RequestBody
            IncomeCreateRequest request
    ) {

        IncomeResponse response =
                incomeService.createIncome(request);

        ApiResponse<IncomeResponse> apiResponse =
                ApiResponse.<IncomeResponse>builder()
                        .success(true)
                        .message("Income created successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<IncomeResponse>>> getAllIncomes(

            IncomeFilterRequest request,

            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "incomeDate",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {

        PagedResponse<IncomeResponse> response =
                incomeService.getAllIncomes(
                        request,
                        pageable
                );

        ApiResponse<PagedResponse<IncomeResponse>> apiResponse =
                ApiResponse.<PagedResponse<IncomeResponse>>builder()
                        .success(true)
                        .message("Income fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IncomeResponse>> getIncomeById(
            @PathVariable UUID id
    ) {

        IncomeResponse response =
                incomeService.getIncomeById(id);

        ApiResponse<IncomeResponse> apiResponse =
                ApiResponse.<IncomeResponse>builder()
                        .success(true)
                        .message("Income fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IncomeResponse>> updateIncome(

            @PathVariable UUID id,
            @Valid
            @RequestBody
            IncomeUpdateRequest request
    ) {

        IncomeResponse response =
                incomeService.updateIncome(id, request);

        ApiResponse<IncomeResponse> apiResponse =
                ApiResponse.<IncomeResponse>builder()
                        .success(true)
                        .message("Income updated successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIncome(
            @PathVariable UUID id
    ) {

        incomeService.deleteIncome(id);

        ApiResponse<Void> response =
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Income deleted successfully")
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);

    }

}