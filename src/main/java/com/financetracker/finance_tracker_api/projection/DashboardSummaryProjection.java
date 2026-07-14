package com.financetracker.finance_tracker_api.projection;

import java.math.BigDecimal;

public interface DashboardSummaryProjection {

    BigDecimal getTotalExpense();

    Long getTotalTransactions();

    BigDecimal getAverageExpense();

    BigDecimal getHighestExpense();

}
