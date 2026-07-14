package com.financetracker.finance_tracker_api.dto.request;

import com.financetracker.finance_tracker_api.entity.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class ExpenseUpdateRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private UUID categoryId;

    @NotNull
    private PaymentMethod paymentMethod;

    @NotNull
    private LocalDate expenseDate;
}
