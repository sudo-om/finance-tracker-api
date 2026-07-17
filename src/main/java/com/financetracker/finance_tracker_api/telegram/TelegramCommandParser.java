package com.financetracker.finance_tracker_api.telegram;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TelegramCommandParser {

    public ParsedExpense parse(String message) {

        String[] parts = message.split("\\s+");

        if (parts.length < 4) {
            throw new IllegalArgumentException(
                    "Invalid format. Use: Spent <amount> <category> <merchant>"
            );
        }

        return ParsedExpense.builder()
                .amount(new BigDecimal(parts[1]))
                .category(parts[2])
                .merchant(parts[3])
                .build();
    }

}