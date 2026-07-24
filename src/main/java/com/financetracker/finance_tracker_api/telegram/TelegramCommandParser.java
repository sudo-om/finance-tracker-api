package com.financetracker.finance_tracker_api.telegram;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TelegramCommandParser {

    public ParsedExpense parse(String message) {

        if (message == null || message.isBlank()) {
            throw invalidFormat();
        }

        String[] parts = message.trim().split("\\s+", 4);

        if (parts.length < 4 || (!"spent".equalsIgnoreCase(parts[0]) && !"/spent".equalsIgnoreCase(parts[0]))) {
            throw invalidFormat();
        }

        try {
            return ParsedExpense.builder()
                    .amount(new BigDecimal(parts[1]))
                    .category(parts[2])
                    .merchant(parts[3])
                    .build();
        } catch (NumberFormatException exception) {
            throw invalidFormat();
        }
    }

    public ParsedIncome parseIncome(String message) {

        if (message == null || message.isBlank()) {
            throw invalidIncomeFormat();
        }

        String[] parts = message.trim().split("\\s+", 3);

        if (parts.length < 3 || (!"income".equalsIgnoreCase(parts[0]) && !"/income".equalsIgnoreCase(parts[0]))) {
            throw invalidIncomeFormat();
        }

        try {
            return ParsedIncome.builder()
                    .amount(new BigDecimal(parts[1]))
                    .source(parts[2])
                    .build();
        } catch (NumberFormatException exception) {
            throw invalidIncomeFormat();
        }
    }

    private IllegalArgumentException invalidFormat() {

        return new IllegalArgumentException(
                "Invalid format. Use: Spent <amount> <category> <merchant>"
        );
    }

    private IllegalArgumentException invalidIncomeFormat() {

        return new IllegalArgumentException(
                "Invalid format. Use: Income <amount> <source>"
        );
    }

}
