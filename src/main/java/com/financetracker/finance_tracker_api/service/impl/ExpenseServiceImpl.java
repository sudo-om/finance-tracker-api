package com.financetracker.finance_tracker_api.service.impl;

import com.financetracker.finance_tracker_api.dto.request.ExpenseCreateRequest;
import com.financetracker.finance_tracker_api.dto.response.ExpenseResponse;
import com.financetracker.finance_tracker_api.entity.Category;
import com.financetracker.finance_tracker_api.entity.Expense;
import com.financetracker.finance_tracker_api.entity.User;
import com.financetracker.finance_tracker_api.exception.ResourceNotFoundException;
import com.financetracker.finance_tracker_api.mapper.ExpenseMapper;
import com.financetracker.finance_tracker_api.repository.CategoryRepository;
import com.financetracker.finance_tracker_api.repository.ExpenseRepository;
import com.financetracker.finance_tracker_api.service.CurrentUserService;
import com.financetracker.finance_tracker_api.service.ExpenseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    private final CategoryRepository categoryRepository;

    private final CurrentUserService currentUserService;

    private final ExpenseMapper expenseMapper;


    @Override
    @Transactional
    public ExpenseResponse createExpense(
            ExpenseCreateRequest request
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        Category category =
                categoryRepository
                        .findById(request.getCategoryId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found"
                                ));

        Expense expense = Expense.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .amount(request.getAmount())
                .expenseDate(request.getExpenseDate())
                .paymentMethod(request.getPaymentMethod())
                .user(currentUser)
                .category(category)
                .build();

        Expense savedExpense =
                expenseRepository.save(expense);

        return expenseMapper.toResponse(savedExpense);

    }

}
