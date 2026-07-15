package com.financetracker.finance_tracker_api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class IncomeResponse {

    private UUID id;

    private BigDecimal amount;

    private String source;

    private String description;

    private LocalDate incomeDate;

    private String category;

    private LocalDateTime createdAt;
}