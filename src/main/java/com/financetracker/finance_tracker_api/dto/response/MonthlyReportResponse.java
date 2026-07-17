package com.financetracker.finance_tracker_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyReportResponse {

    private BigDecimal totalIncome;

    private BigDecimal totalExpense;

    private BigDecimal totalSavings;

    private BigDecimal savingsRate;

    private BigDecimal largestExpense;

    private BigDecimal largestIncome;

    private String topExpenseCategory;

    private String topIncomeSource;

}