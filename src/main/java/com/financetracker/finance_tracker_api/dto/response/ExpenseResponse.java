package com.financetracker.finance_tracker_api.dto.response;

import com.financetracker.finance_tracker_api.entity.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ExpenseResponse {

    private Long id;

    private String title;

    private String description;

    private BigDecimal amount;

    private String category;

    private PaymentMethod paymentMethod;

    private LocalDate expenseDate;

    private LocalDateTime createdAt;

}