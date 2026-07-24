package com.financetracker.finance_tracker_api.telegram;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ParsedIncome {

    private BigDecimal amount;

    private String source;

}
