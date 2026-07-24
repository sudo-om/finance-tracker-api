package com.financetracker.finance_tracker_api.telegram;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TelegramCommandParserTest {

    private final TelegramCommandParser parser = new TelegramCommandParser();

    @Test
    void parsesSpentCommandWithMultiWordMerchant() {

        ParsedExpense expense =
                parser.parse("Spent 450 Food Domino's Pizza");

        assertEquals(new BigDecimal("450"), expense.getAmount());
        assertEquals("Food", expense.getCategory());
        assertEquals("Domino's Pizza", expense.getMerchant());
    }

    @Test
    void rejectsMessagesThatAreNotSpentCommands() {

        assertThrows(IllegalArgumentException.class,
                () -> parser.parse("Hello 450 Food Domino's"));
    }

    @Test
    void rejectsInvalidAmounts() {

        assertThrows(IllegalArgumentException.class,
                () -> parser.parse("Spent abc Food Domino's"));
    }

    @Test
    void parsesIncomeCommandWithMultiWordSource() {

        ParsedIncome income =
                parser.parseIncome("Income 50000 Freelance Work");

        assertEquals(new BigDecimal("50000"), income.getAmount());
        assertEquals("Freelance Work", income.getSource());
    }

    @Test
    void rejectsInvalidIncomeAmounts() {

        assertThrows(IllegalArgumentException.class,
                () -> parser.parseIncome("Income abc Salary"));
    }

}
