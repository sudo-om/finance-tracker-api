package com.financetracker.finance_tracker_api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class IncomeCreateRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @NotBlank(message = "Source is required")
    private String source;

    private String description;

    @NotNull(message = "Income date is required")
    private LocalDate incomeDate;

    @NotNull(message = "Category is required")
    private UUID categoryId;

}
