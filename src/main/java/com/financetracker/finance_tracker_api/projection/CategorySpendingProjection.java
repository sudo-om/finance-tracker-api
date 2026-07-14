package com.financetracker.finance_tracker_api.projection;

import java.math.BigDecimal;

public interface CategorySpendingProjection {

    String getCategory();

    BigDecimal getTotal();

}
