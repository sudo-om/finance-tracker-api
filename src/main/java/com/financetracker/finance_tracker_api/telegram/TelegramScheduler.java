package com.financetracker.finance_tracker_api.telegram;

import com.financetracker.finance_tracker_api.dto.request.ExpenseCreateRequest;
import com.financetracker.finance_tracker_api.dto.response.ExpenseResponse;
import com.financetracker.finance_tracker_api.entity.Category;
import com.financetracker.finance_tracker_api.entity.User;
import com.financetracker.finance_tracker_api.entity.enums.PaymentMethod;
import com.financetracker.finance_tracker_api.exception.ResourceNotFoundException;
import com.financetracker.finance_tracker_api.repository.CategoryRepository;
import com.financetracker.finance_tracker_api.repository.UserRepository;
import com.financetracker.finance_tracker_api.service.ExpenseService;
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

            ParsedExpense parsedExpense =
                    parser.parse(update.getMessage().getText());

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
                            update.getMessage().getChat().getId()
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Telegram account not linked."
                            ));

            ExpenseResponse expenseResponse =
                    expenseService.createExpense(request, user);

            log.info("Expense Created Successfully : {}",
                    expenseResponse.getId());

        }

    }

}