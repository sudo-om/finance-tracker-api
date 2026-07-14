package com.financetracker.finance_tracker_api.dto.request;

import com.financetracker.finance_tracker_api.entity.enums.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class ExpenseFilterRequest {

    private String search;

    private UUID categoryId;

    private PaymentMethod paymentMethod;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    private LocalDate startDate;

    private LocalDate endDate;
}