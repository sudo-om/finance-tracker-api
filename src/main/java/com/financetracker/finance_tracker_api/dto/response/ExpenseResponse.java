package com.financetracker.finance_tracker_api.dto.response;

import com.financetracker.finance_tracker_api.entity.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ExpenseResponse {

    private UUID id;

    private String title;

    private String description;

    private BigDecimal amount;

    private String category;

    private PaymentMethod paymentMethod;

    private LocalDate expenseDate;

    private LocalDateTime createdAt;

}