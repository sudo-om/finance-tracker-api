package com.financetracker.finance_tracker_api.telegram;

import com.financetracker.finance_tracker_api.dto.request.BudgetCreateRequest;
import com.financetracker.finance_tracker_api.dto.request.ExpenseCreateRequest;
import com.financetracker.finance_tracker_api.dto.request.IncomeCreateRequest;
import com.financetracker.finance_tracker_api.dto.response.BalanceResponse;
import com.financetracker.finance_tracker_api.dto.response.ExpenseResponse;
import com.financetracker.finance_tracker_api.dto.response.IncomeResponse;
import com.financetracker.finance_tracker_api.dto.response.DashboardSummaryResponse;
import com.financetracker.finance_tracker_api.dto.response.BudgetResponse;
import com.financetracker.finance_tracker_api.entity.Budget;
import com.financetracker.finance_tracker_api.entity.Category;
import com.financetracker.finance_tracker_api.entity.User;
import com.financetracker.finance_tracker_api.entity.enums.CategoryType;
import com.financetracker.finance_tracker_api.entity.enums.PaymentMethod;
import com.financetracker.finance_tracker_api.exception.ResourceNotFoundException;
import com.financetracker.finance_tracker_api.repository.BudgetRepository;
import com.financetracker.finance_tracker_api.repository.CategoryRepository;
import com.financetracker.finance_tracker_api.repository.UserRepository;
import com.financetracker.finance_tracker_api.service.ExpenseService;
import com.financetracker.finance_tracker_api.service.DashboardService;
import com.financetracker.finance_tracker_api.service.IncomeService;
import com.financetracker.finance_tracker_api.service.BudgetService;
import com.financetracker.finance_tracker_api.service.TelegramLinkService;
import com.financetracker.finance_tracker_api.telegram.dto.TelegramUpdate;
import com.financetracker.finance_tracker_api.telegram.dto.TelegramUpdateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

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
    private final BudgetRepository budgetRepository;
    private final TelegramLinkService telegramLinkService;

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void pollTelegram() {

        try {
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
        } catch (Exception exception) {
            log.error("Error polling Telegram updates: {}", exception.getMessage());
        }

    }

    @Transactional
    public void processUpdate(TelegramUpdate update) {

        if (update == null || update.getMessage() == null ||
                update.getMessage().getChat() == null ||
                update.getMessage().getChat().getId() == null ||
                update.getMessage().getText() == null ||
                update.getMessage().getText().isBlank()) {
            return;
        }

        Long chatId = update.getMessage().getChat().getId();
        String message = update.getMessage().getText().trim();

        if (startsWithCommand(message, "start")) {
            String code = extractArgument(message, "start");
            if (code.isEmpty()) {
                telegramService.sendMessage(chatId,
                        "Welcome to Finance Tracker! Use /help to see available commands.");
            } else {
                handleLinkCommand(chatId, code);
            }
            return;
        }

        if ("/help".equalsIgnoreCase(message) || "help".equalsIgnoreCase(message)) {
            telegramService.sendMessage(chatId,
                    "📋 *Available Commands:*\n\n" +
                            "• `/link <code>` — Link Telegram account\n" +
                            "• `/categories` — List available categories\n" +
                            "• `/spent <amount> <category> <merchant>` — Add expense\n" +
                            "• `/income <amount> <source>` — Add income\n" +
                            "• `/balance` — View current balance\n" +
                            "• `/summary` — View spending summary\n" +
                            "• `/budgets` — View all active budgets\n" +
                            "• `/budget <category>` — View budget for a category\n" +
                            "• `/setbudget <amount> <category> [period]` — Set budget (e.g. `/setbudget 5000 Food monthly`)\n\n" +
                            "💡 *Examples:*\n" +
                            "• `/spent 450 Food Domino's`\n" +
                            "• `/income 50000 Salary`\n" +
                            "• `/setbudget 5000 Food monthly`");
            return;
        }

        if (startsWithCommand(message, "link")) {
            String code = extractArgument(message, "link");
            handleLinkCommand(chatId, code);
            return;
        }

        if (startsWithCommand(message, "categories") || startsWithCommand(message, "listcategories")) {
            sendCategories(chatId);
            return;
        }

        if (startsWithCommand(message, "budgets") || startsWithCommand(message, "listbudgets")) {
            sendAllBudgets(chatId);
            return;
        }

        if (startsWithCommand(message, "setbudget") || startsWithCommand(message, "addbudget")) {
            handleAddBudget(chatId, message);
            return;
        }

        if (startsWithCommand(message, "budget")) {
            sendBudget(chatId, message);
            return;
        }

        if ("Summary".equalsIgnoreCase(message) || "/Summary".equalsIgnoreCase(message)) {
            sendSummary(chatId);
            return;
        }

        if ("Balance".equalsIgnoreCase(message) || "/Balance".equalsIgnoreCase(message)) {
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

    private void handleLinkCommand(Long chatId, String code) {

        if (code == null || code.isBlank()) {
            telegramService.sendMessage(chatId,
                    "Please provide your link code.\n" +
                            "Usage: /link <code>\n" +
                            "Example: /link A3B7K9X2\n\n" +
                            "Generate a code from the Finance Tracker app first.");
            return;
        }

        try {
            telegramLinkService.linkChatId(code, chatId);
            telegramService.sendMessage(chatId,
                    "✅ Telegram linked successfully!\n\n" +
                            "You can now use commands like:\n" +
                            "• /spent 450 Food Domino's\n" +
                            "• /income 50000 Salary\n" +
                            "• /balance\n" +
                            "• /summary");
        } catch (ResourceNotFoundException | IllegalArgumentException exception) {
            telegramService.sendMessage(chatId,
                    "❌ " + exception.getMessage());
        } catch (Exception exception) {
            log.error("Unexpected error while linking Telegram account", exception);
            telegramService.sendMessage(chatId,
                    "Something went wrong while linking your account. Please try again.");
        }
    }

    private String extractArgument(String message, String command) {
        String trimmed = message.trim();
        if (trimmed.equalsIgnoreCase(command) || trimmed.equalsIgnoreCase("/" + command)) {
            return "";
        }
        if (trimmed.toLowerCase().startsWith("/" + command.toLowerCase())) {
            return trimmed.substring(command.length() + 1).trim();
        }
        if (trimmed.toLowerCase().startsWith(command.toLowerCase())) {
            return trimmed.substring(command.length()).trim();
        }
        return "";
    }

    private boolean startsWithCommand(String message, String command) {

        if (message.regionMatches(true, 0, command, 0, command.length()) &&
                (message.length() == command.length() ||
                        Character.isWhitespace(message.charAt(command.length())))) {
            return true;
        }

        String slashCommand = "/" + command;
        return message.regionMatches(true, 0, slashCommand, 0, slashCommand.length()) &&
                (message.length() == slashCommand.length() ||
                        Character.isWhitespace(message.charAt(slashCommand.length())));
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

    private void sendCategories(Long chatId) {
        List<Category> allCategories = categoryRepository.findAll();

        List<String> expenseCategories = allCategories.stream()
                .filter(c -> c.getType() == CategoryType.EXPENSE)
                .map(Category::getName)
                .sorted()
                .toList();

        List<String> incomeCategories = allCategories.stream()
                .filter(c -> c.getType() == CategoryType.INCOME)
                .map(Category::getName)
                .sorted()
                .toList();

        StringBuilder sb = new StringBuilder("📂 *Available Categories:*\n\n");

        sb.append("💸 *Expense Categories:*\n");
        if (expenseCategories.isEmpty()) {
            sb.append("_(None registered)_\n");
        } else {
            for (String cat : expenseCategories) {
                sb.append("• ").append(cat).append("\n");
            }
        }

        sb.append("\n💰 *Income Categories:*\n");
        if (incomeCategories.isEmpty()) {
            sb.append("_(None registered)_\n");
        } else {
            for (String cat : incomeCategories) {
                sb.append("• ").append(cat).append("\n");
            }
        }

        sb.append("\n💡 *Usage:* Use these categories with `/spent`, `/income`, or `/setbudget`.");
        telegramService.sendMessage(chatId, sb.toString());
    }

    @Transactional(readOnly = true)
    public void sendAllBudgets(Long chatId) {
        try {
            User user = userRepository.findByTelegramChatId(chatId)
                    .orElseThrow(() -> new ResourceNotFoundException("Telegram account not linked."));

            List<Budget> budgets = budgetRepository.findByUser(user);

            if (budgets.isEmpty()) {
                telegramService.sendMessage(chatId,
                        "ℹ️ You have no active budgets.\n" +
                                "Use `/setbudget <amount> <category>` to create one.\n" +
                                "Example: `/setbudget 5000 Food`");
                return;
            }

            StringBuilder sb = new StringBuilder("📊 *Your Active Budgets:*\n\n");
            for (Budget b : budgets) {
                try {
                    BudgetResponse resp = budgetService.toBudgetResponse(b, user);
                    sb.append("• *").append(resp.getCategory()).append("*\n")
                            .append("  Limit: ₹").append(resp.getAmount())
                            .append(" | Spent: ₹").append(resp.getSpent())
                            .append(" | Remaining: ₹").append(resp.getRemaining())
                            .append("\n  Period: ").append(resp.getStartDate()).append(" to ").append(resp.getEndDate())
                            .append("\n  Used: ").append(resp.getPercentageUsed()).append("% [").append(resp.getStatus()).append("]\n\n");
                } catch (Exception e) {
                    sb.append("• *").append(b.getCategory().getName()).append("*: Limit ₹").append(b.getAmount()).append("\n\n");
                }
            }
            telegramService.sendMessage(chatId, sb.toString());
        } catch (ResourceNotFoundException e) {
            telegramService.sendMessage(chatId, "❌ " + e.getMessage());
        } catch (Exception e) {
            log.error("Error sending budgets for chat {}", chatId, e);
            telegramService.sendMessage(chatId, "Something went wrong while fetching budgets.");
        }
    }

    private void handleAddBudget(Long chatId, String message) {
        String args;
        if (startsWithCommand(message, "setbudget")) {
            args = extractArgument(message, "setbudget");
        } else {
            args = extractArgument(message, "addbudget");
        }

        if (args.isBlank()) {
            telegramService.sendMessage(chatId,
                    "Please provide budget details.\n\n" +
                            "📌 *Usage Options:*\n" +
                            "• `/setbudget <amount> <category>` (Default: Monthly)\n" +
                            "• `/setbudget <amount> <category> <period>` (`weekly`, `monthly`, `yearly`)\n" +
                            "• `/setbudget <amount> <category> <startDate> <endDate>` (YYYY-MM-DD)\n\n" +
                            "💡 *Examples:*\n" +
                            "• `/setbudget 5000 Food`\n" +
                            "• `/setbudget 1000 Food weekly`\n" +
                            "• `/setbudget 5000 Food 2026-07-01 2026-07-31`\n\n" +
                            "Use `/categories` to see available categories.");
            return;
        }

        try {
            User user = userRepository.findByTelegramChatId(chatId)
                    .orElseThrow(() -> new ResourceNotFoundException("Telegram account not linked."));

            String[] parts = args.trim().split("\\s+");
            if (parts.length < 2) {
                telegramService.sendMessage(chatId, "Invalid command. Usage: `/setbudget <amount> <category> [period]`");
                return;
            }

            BigDecimal amount = new BigDecimal(parts[0]);
            String categoryName = parts[1];

            Category category = categoryRepository.findByNameIgnoreCase(categoryName)
                    .orElseThrow(() -> new ResourceNotFoundException("Category '" + categoryName + "' not found. Use `/categories` to see available categories."));

            LocalDate startDate;
            LocalDate endDate;

            if (parts.length == 2) {
                startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
                endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
            } else if (parts.length == 3) {
                String period = parts[2].toLowerCase();
                switch (period) {
                    case "weekly" -> {
                        startDate = LocalDate.now().with(DayOfWeek.MONDAY);
                        endDate = LocalDate.now().with(DayOfWeek.SUNDAY);
                    }
                    case "yearly" -> {
                        startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
                        endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
                    }
                    case "monthly" -> {
                        startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
                        endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
                    }
                    default -> {
                        telegramService.sendMessage(chatId, "Invalid period '" + parts[2] + "'. Use `weekly`, `monthly`, or `yearly`.");
                        return;
                    }
                }
            } else {
                startDate = LocalDate.parse(parts[2]);
                endDate = LocalDate.parse(parts[3]);
            }

            BudgetCreateRequest request = new BudgetCreateRequest();
            request.setAmount(amount);
            request.setCategoryId(category.getId());
            request.setStartDate(startDate);
            request.setEndDate(endDate);

            BudgetResponse response = budgetService.createBudget(request, user);

            telegramService.sendMessage(chatId, String.format(
                    "✅ *Budget Created Successfully!*\n\n" +
                            "• *Category:* %s\n" +
                            "• *Amount Limit:* ₹%s\n" +
                            "• *Period:* %s to %s\n" +
                            "• *Status:* %s",
                    response.getCategory(),
                    response.getAmount(),
                    response.getStartDate(),
                    response.getEndDate(),
                    response.getStatus()
            ));

        } catch (NumberFormatException e) {
            telegramService.sendMessage(chatId, "❌ Invalid amount format. Example: `/setbudget 5000 Food`");
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            telegramService.sendMessage(chatId, "❌ " + e.getMessage());
        } catch (Exception e) {
            log.error("Error creating budget via Telegram", e);
            telegramService.sendMessage(chatId, "Something went wrong while creating your budget.");
        }
    }

    private void sendBudget(Long chatId, String message) {

        String categoryName = extractArgument(message, "budget");

        if (categoryName.isBlank()) {
            telegramService.sendMessage(chatId,
                    "Invalid format. Use: `/budget <category>`\n" +
                            "Example: `/budget Food`\n" +
                            "To see all budgets, use `/budgets`");
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
