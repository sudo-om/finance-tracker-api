package com.financetracker.finance_tracker_api.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardSummaryResponse {

    private BigDecimal totalExpense;

    private Long totalTransactions;

    private BigDecimal averageExpense;

    private BigDecimal highestExpense;

}