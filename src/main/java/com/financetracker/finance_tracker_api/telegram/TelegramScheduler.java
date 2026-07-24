package com.financetracker.finance_tracker_api.telegram;

import com.financetracker.finance_tracker_api.dto.request.ExpenseCreateRequest;
import com.financetracker.finance_tracker_api.dto.request.IncomeCreateRequest;
import com.financetracker.finance_tracker_api.dto.response.BalanceResponse;
import com.financetracker.finance_tracker_api.dto.response.ExpenseResponse;
import com.financetracker.finance_tracker_api.dto.response.IncomeResponse;
import com.financetracker.finance_tracker_api.dto.response.DashboardSummaryResponse;
import com.financetracker.finance_tracker_api.dto.response.BudgetResponse;
import com.financetracker.finance_tracker_api.entity.Category;
import com.financetracker.finance_tracker_api.entity.User;
import com.financetracker.finance_tracker_api.entity.enums.CategoryType;
import com.financetracker.finance_tracker_api.entity.enums.PaymentMethod;
import com.financetracker.finance_tracker_api.exception.ResourceNotFoundException;
import com.financetracker.finance_tracker_api.repository.CategoryRepository;
import com.financetracker.finance_tracker_api.repository.UserRepository;
import com.financetracker.finance_tracker_api.service.ExpenseService;
import com.financetracker.finance_tracker_api.service.DashboardService;
import com.financetracker.finance_tracker_api.service.IncomeService;
import com.financetracker.finance_tracker_api.service.BudgetService;
import com.financetracker.finance_tracker_api.telegram.dto.TelegramUpdate;
import com.financetracker.finance_tracker_api.telegram.dto.TelegramUpdateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramScheduler {

    private final TelegramPollingService pollingService;
    private final TelegramCommandParser parser;
    private final ExpenseService expenseService;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TelegramService telegramService;
    private final IncomeService incomeService;
    private final DashboardService dashboardService;
    private final BudgetService budgetService;

    @Scheduled(fixedDelay = 2000)
    public void pollTelegram() {

        TelegramUpdateResponse response =
                pollingService.getUpdates();

        if (response == null ||
                response.getResult() == null ||
                response.getResult().isEmpty()) {
            return;
        }

        for (TelegramUpdate update : response.getResult()) {
            processUpdate(update);
        }

    }

    private void processUpdate(TelegramUpdate update) {

        if (update == null || update.getMessage() == null ||
                update.getMessage().getChat() == null ||
                update.getMessage().getChat().getId() == null ||
                update.getMessage().getText() == null ||
                update.getMessage().getText().isBlank()) {
            return;
        }

        Long chatId = update.getMessage().getChat().getId();
        String message = update.getMessage().getText().trim();

        if ("/start".equalsIgnoreCase(message)) {
            telegramService.sendMessage(chatId,
                    "Welcome to Finance Tracker! Use /help to see available commands.");
            return;
        }

        if ("/help".equalsIgnoreCase(message)) {
            telegramService.sendMessage(chatId,
                    "Available commands:\n" +
                            "Spent <amount> <category> <merchant>\n" +
                            "Income <amount> <source>\n\n" +
                            "Balance\n\n" +
                            "Summary\n\n" +
                            "Budget <category>\n\n" +
                            "Example: Spent 450 Food Domino's");
            return;
        }

        if (startsWithCommand(message, "Budget")) {
            sendBudget(chatId, message);
            return;
        }

        if ("Summary".equalsIgnoreCase(message)) {
            sendSummary(chatId);
            return;
        }

        if ("Balance".equalsIgnoreCase(message)) {
            sendBalance(chatId);
            return;
        }

        if (startsWithCommand(message, "Income")) {
            createIncome(chatId, message);
            return;
        }

        if (!startsWithCommand(message, "Spent")) {
            telegramService.sendMessage(chatId,
                    "I don't understand that command. Use /help to see available commands.");
            return;
        }

        try {
            ParsedExpense parsedExpense = parser.parse(message);

            Category category = categoryRepository
                    .findByNameIgnoreCase(parsedExpense.getCategory())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Category not found"));

            ExpenseCreateRequest request = new ExpenseCreateRequest();

            request.setTitle(parsedExpense.getMerchant());
            request.setMerchant(parsedExpense.getMerchant());
            request.setDescription("Created from Telegram");
            request.setAmount(parsedExpense.getAmount());
            request.setExpenseDate(LocalDate.now());
            request.setPaymentMethod(PaymentMethod.UPI);
            request.setCategoryId(category.getId());

            User user = userRepository
                    .findByTelegramChatId(
                            chatId
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Telegram account not linked."
                            ));

            ExpenseResponse expenseResponse =
                    expenseService.createExpense(request, user);

            log.info("Expense Created Successfully : {}",
                    expenseResponse.getId());

        } catch (IllegalArgumentException exception) {
            telegramService.sendMessage(chatId,
                    "Invalid format. Use: Spent <amount> <category> <merchant>\n" +
                            "Example: Spent 450 Food Domino's");
        } catch (ResourceNotFoundException exception) {
            log.warn("Unable to create an expense for Telegram chat {}: {}",
                    chatId, exception.getMessage());
            telegramService.sendMessage(chatId, exception.getMessage());
        } catch (Exception exception) {
            log.error("Unexpected error while processing Telegram update", exception);
            telegramService.sendMessage(chatId,
                    "Something went wrong while processing your message. Please try again.");
        }

    }

    private void createIncome(Long chatId, String message) {

        try {
            ParsedIncome parsedIncome = parser.parseIncome(message);

            User user = userRepository
                    .findByTelegramChatId(chatId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Telegram account not linked."
                            ));

            Category category = categoryRepository
                    .findByNameIgnoreCase(parsedIncome.getSource())
                    .filter(foundCategory ->
                            foundCategory.getType() == CategoryType.INCOME)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Income category not found. Create the " +
                                            parsedIncome.getSource() +
                                            " income category first."
                            ));

            IncomeCreateRequest request = new IncomeCreateRequest();
            request.setAmount(parsedIncome.getAmount());
            request.setSource(parsedIncome.getSource());
            request.setDescription("Created from Telegram");
            request.setIncomeDate(LocalDate.now());
            request.setCategoryId(category.getId());

            IncomeResponse incomeResponse =
                    incomeService.createIncome(request, user);

            log.info("Income created successfully: {}", incomeResponse.getId());
            telegramService.sendMessage(chatId,
                    "Income recorded: " + parsedIncome.getAmount() +
                            " from " + parsedIncome.getSource() + ".");
        } catch (IllegalArgumentException exception) {
            telegramService.sendMessage(chatId,
                    "Invalid format. Use: Income <amount> <source>\n" +
                            "Example: Income 50000 Salary");
        } catch (ResourceNotFoundException exception) {
            log.warn("Unable to create income for Telegram chat {}: {}",
                    chatId, exception.getMessage());
            telegramService.sendMessage(chatId, exception.getMessage());
        } catch (Exception exception) {
            log.error("Unexpected error while processing Telegram income", exception);
            telegramService.sendMessage(chatId,
                    "Something went wrong while processing your income. Please try again.");
        }
    }

    private boolean startsWithCommand(String message, String command) {

        return message.regionMatches(true, 0, command, 0, command.length()) &&
                (message.length() == command.length() ||
                        Character.isWhitespace(message.charAt(command.length())));
    }

    private void sendBalance(Long chatId) {

        try {
            User user = userRepository
                    .findByTelegramChatId(chatId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Telegram account not linked."
                            ));

            BalanceResponse balance = dashboardService.getBalance(user);

            telegramService.sendMessage(chatId,
                    "Your balance:\n" +
                            "Income: " + balance.getTotalIncome() + "\n" +
                            "Expenses: " + balance.getTotalExpense() + "\n" +
                            "Balance: " + balance.getCurrentBalance() + "\n" +
                            "Savings rate: " + balance.getSavingsRate() + "%");
        } catch (ResourceNotFoundException exception) {
            telegramService.sendMessage(chatId, exception.getMessage());
        } catch (Exception exception) {
            log.error("Unexpected error while retrieving Telegram balance", exception);
            telegramService.sendMessage(chatId,
                    "Something went wrong while retrieving your balance. Please try again.");
        }
    }

    private void sendSummary(Long chatId) {

        try {
            User user = userRepository
                    .findByTelegramChatId(chatId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Telegram account not linked."
                            ));

            DashboardSummaryResponse summary =
                    dashboardService.getSummary(user);

            telegramService.sendMessage(chatId,
                    "Spending summary:\n" +
                            "Total spent: " + summary.getTotalExpense() + "\n" +
                            "Transactions: " + summary.getTotalTransactions() + "\n" +
                            "Average expense: " + summary.getAverageExpense() + "\n" +
                            "Largest expense: " + summary.getHighestExpense());
        } catch (ResourceNotFoundException exception) {
            telegramService.sendMessage(chatId, exception.getMessage());
        } catch (Exception exception) {
            log.error("Unexpected error while retrieving Telegram summary", exception);
            telegramService.sendMessage(chatId,
                    "Something went wrong while retrieving your summary. Please try again.");
        }
    }

    private void sendBudget(Long chatId, String message) {

        String categoryName = message.substring("Budget".length()).trim();

        if (categoryName.isBlank()) {
            telegramService.sendMessage(chatId,
                    "Invalid format. Use: Budget <category>\n" +
                            "Example: Budget Food");
            return;
        }

        try {
            User user = userRepository
                    .findByTelegramChatId(chatId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Telegram account not linked."
                            ));

            BudgetResponse budget =
                    budgetService.getCurrentBudget(user, categoryName);

            telegramService.sendMessage(chatId,
                    "Budget for " + budget.getCategory() + ":\n" +
                            "Limit: " + budget.getAmount() + "\n" +
                            "Spent: " + budget.getSpent() + "\n" +
                            "Remaining: " + budget.getRemaining() + "\n" +
                            "Used: " + budget.getPercentageUsed() + "%\n" +
                            "Status: " + budget.getStatus());
        } catch (ResourceNotFoundException exception) {
            telegramService.sendMessage(chatId, exception.getMessage());
        } catch (Exception exception) {
            log.error("Unexpected error while retrieving Telegram budget", exception);
            telegramService.sendMessage(chatId,
                    "Something went wrong while retrieving your budget. Please try again.");
        }
    }

}
