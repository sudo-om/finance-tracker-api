package com.financetracker.finance_tracker_api.projection;

import java.math.BigDecimal;

public interface MonthlyExpenseProjection {

    Integer getMonth();

    BigDecimal getTotalExpense();

    BigDecimal getTotal();
}