package com.financetracker.finance_tracker_api.dto.response;

import com.financetracker.finance_tracker_api.entity.enums.BudgetStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BudgetResponse {

    private UUID id;

    private BigDecimal amount;

    private BigDecimal spent;

    private BigDecimal remaining;

    private BigDecimal percentageUsed;

    private BudgetStatus status;

    private LocalDate startDate;

    private LocalDate endDate;

    private String category;

    private LocalDateTime createdAt;

}