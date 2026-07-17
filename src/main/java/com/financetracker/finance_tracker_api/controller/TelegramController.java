package com.financetracker.finance_tracker_api.controller;

import com.financetracker.finance_tracker_api.dto.request.ExpenseCreateRequest;
import com.financetracker.finance_tracker_api.dto.response.ExpenseResponse;
import com.financetracker.finance_tracker_api.entity.Category;
import com.financetracker.finance_tracker_api.entity.User;
import com.financetracker.finance_tracker_api.entity.enums.PaymentMethod;
import com.financetracker.finance_tracker_api.exception.ResourceNotFoundException;
import com.financetracker.finance_tracker_api.repository.CategoryRepository;
import com.financetracker.finance_tracker_api.repository.UserRepository;
import com.financetracker.finance_tracker_api.service.ExpenseService;
import com.financetracker.finance_tracker_api.service.TelegramUserService;
import com.financetracker.finance_tracker_api.telegram.ParsedExpense;
import com.financetracker.finance_tracker_api.telegram.TelegramCommandParser;
import com.financetracker.finance_tracker_api.telegram.TelegramPollingService;
import com.financetracker.finance_tracker_api.telegram.TelegramService;
import com.financetracker.finance_tracker_api.telegram.dto.TelegramUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/telegram")
@RequiredArgsConstructor
public class TelegramController {

    private final TelegramService telegramService;
    private final TelegramUserService telegramUserService;
    private final TelegramCommandParser parser;
    private final ExpenseService expenseService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TelegramPollingService telegramPollingService;

    @PostMapping("/link")
    public String link() {

        telegramUserService.linkTelegram(656552585L);

        return "Telegram linked successfully";
    }

    @GetMapping("/test")
    public String test() {

        telegramService.sendMessage(
                "🚀 Finance Tracker Connected Successfully!"
        );

        return "Message Sent";
    }

    @GetMapping("/parse")
    public ParsedExpense parse() {

        return parser.parse("Spent 450 Food Domino's");

    }

    @PostMapping("/expense")
    public ExpenseResponse createExpense() {

        ParsedExpense parsedExpense =
                parser.parse("Spent 450 Food Domino's");

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
                .findByTelegramChatId(917128149L)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Telegram account not linked"));

        return expenseService.createExpense(request, user);
    }

    @GetMapping("/updates")
    public TelegramUpdateResponse updates() {

        return telegramPollingService.getUpdates();

    }
}