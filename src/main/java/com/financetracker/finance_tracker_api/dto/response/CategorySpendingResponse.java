package com.financetracker.finance_tracker_api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CategorySpendingResponse {

    private String category;

    private BigDecimal total;

}
