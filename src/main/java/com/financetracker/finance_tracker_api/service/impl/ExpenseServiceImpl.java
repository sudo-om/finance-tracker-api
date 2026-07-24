package com.financetracker.finance_tracker_api.service.impl;

import com.financetracker.finance_tracker_api.dto.request.ExpenseCreateRequest;
import com.financetracker.finance_tracker_api.dto.request.ExpenseFilterRequest;
import com.financetracker.finance_tracker_api.dto.request.ExpenseUpdateRequest;
import com.financetracker.finance_tracker_api.dto.response.ExpenseResponse;
import com.financetracker.finance_tracker_api.dto.response.PagedResponse;
import com.financetracker.finance_tracker_api.dto.response.Pagination;
import com.financetracker.finance_tracker_api.entity.Category;
import com.financetracker.finance_tracker_api.entity.Expense;
import com.financetracker.finance_tracker_api.entity.User;
import com.financetracker.finance_tracker_api.exception.ResourceNotFoundException;
import com.financetracker.finance_tracker_api.mapper.ExpenseMapper;
import com.financetracker.finance_tracker_api.repository.CategoryRepository;
import com.financetracker.finance_tracker_api.repository.ExpenseRepository;
import com.financetracker.finance_tracker_api.service.CurrentUserService;
import com.financetracker.finance_tracker_api.service.ExpenseService;
import com.financetracker.finance_tracker_api.specification.ExpenseSpecificationBuilder;

import com.financetracker.finance_tracker_api.telegram.TelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    private final CategoryRepository categoryRepository;

    private final CurrentUserService currentUserService;

    private final ExpenseMapper expenseMapper;

    private final ExpenseSpecificationBuilder expenseSpecificationBuilder;

    private final TelegramService telegramService;

    private String buildExpenseMessage(Expense expense) {

        return """
            💸 Expense Added Successfully

            💰 Amount : ₹%s
            📂 Category : %s
            🏪 Merchant : %s
            📅 Date : %s

            """.formatted(
                expense.getAmount(),
                expense.getCategory().getName(),
                expense.getMerchant(),
                expense.getExpenseDate()
        );
    }


    @Override
    public ExpenseResponse createExpense(
            ExpenseCreateRequest request
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        return createExpense(request, currentUser);

    }

    @Override
    public ExpenseResponse createExpense(
            ExpenseCreateRequest request,
            User user
    ) {

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + request.getCategoryId()
                        ));

        Expense expense = Expense.builder()
                .title(request.getTitle())
                .merchant(request.getMerchant())
                .description(request.getDescription())
                .amount(request.getAmount())
                .expenseDate(request.getExpenseDate())
                .paymentMethod(request.getPaymentMethod())
                .user(user)
                .category(category)
                .build();

        Expense savedExpense = expenseRepository.saveAndFlush(expense);

        /*
         * Bug fix 2: Send the Telegram notification to the expense owner's
         * actual chatId, not the hardcoded telegram.chat.id from config.
         * sendMessage(String) uses a hardcoded chatId — it must never be called
         * here because it would notify a fixed account regardless of who made
         * the expense. Only notify if the user has Telegram linked.
         */
        if (user.getTelegramChatId() != null) {
            telegramService.sendMessage(
                    user.getTelegramChatId(),
                    buildExpenseMessage(savedExpense)
            );
        }

        return expenseMapper.toResponse(savedExpense);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ExpenseResponse> getAllExpenses(ExpenseFilterRequest request, Pageable pageable) {

        User currentUser = currentUserService.getCurrentUser();

        Specification<Expense> specification =
                expenseSpecificationBuilder.build(
                        request,
                        currentUser
                );

        Page<Expense> expensePage =
                expenseRepository.findAll(
                        specification,
                        pageable
                );

        List<ExpenseResponse> expenses =
                expensePage.getContent()
                        .stream()
                        .map(expenseMapper::toResponse)
                        .toList();

        Pagination pagination = Pagination.builder()
                .page(expensePage.getNumber())
                .size(expensePage.getSize())
                .totalElements(expensePage.getTotalElements())
                .totalPages(expensePage.getTotalPages())
                .first(expensePage.isFirst())
                .last(expensePage.isLast())
                .build();

        return PagedResponse.<ExpenseResponse>builder()
                .content(expenses)
                .pagination(pagination)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(UUID id) {
        User currentUser =
                currentUserService.getCurrentUser();

        Expense expense =
                expenseRepository
                        .findByIdAndUser(id, currentUser)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Expense not found"
                                ));
        return expenseMapper.toResponse(expense);
    }

    @Override
    public ExpenseResponse updateExpense(UUID id, ExpenseUpdateRequest request) {
        User currentUser =
                currentUserService.getCurrentUser();
        Expense expense =
                expenseRepository
                        .findByIdAndUser(id, currentUser)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Expense not found"
                                ));

        Category category =
                categoryRepository
                        .findById(request.getCategoryId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found with id: " + request.getCategoryId()
                                ));
        expense.setTitle(request.getTitle());
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setPaymentMethod(request.getPaymentMethod());
        expense.setCategory(category);
        expenseRepository.save(expense);

        return expenseMapper.toResponse(expense);
    }

    @Override
    @Transactional
    public void deleteExpense(UUID id) {

        User currentUser = currentUserService.getCurrentUser();

        Expense expense = expenseRepository
                .findByIdAndUser(id, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Expense not found"
                        ));

        expenseRepository.delete(expense);
    }

}
