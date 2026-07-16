package com.financetracker.finance_tracker_api.dto.response;

import com.financetracker.finance_tracker_api.entity.enums.FinancialGrade;
import com.financetracker.finance_tracker_api.entity.enums.FinancialHealthStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class FinancialHealthResponse {

    private Integer healthScore;

    private FinancialGrade grade;

    private FinancialHealthStatus status;

    private BigDecimal totalIncome;

    private BigDecimal totalExpense;

    private BigDecimal currentBalance;

    private BigDecimal savingsRate;

    private Integer budgetAdherence;

    private List<String> recommendations;

}
