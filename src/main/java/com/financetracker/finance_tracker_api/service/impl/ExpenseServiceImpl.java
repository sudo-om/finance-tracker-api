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
import com.financetracker.finance_tracker_api.specification.ExpenseSpecification;
import com.financetracker.finance_tracker_api.specification.ExpenseSpecificationBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

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


    @Override
    @Transactional
    public ExpenseResponse createExpense(
            ExpenseCreateRequest request
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        Expense expense = Expense.builder()
                .title(request.getTitle())
                .merchant(request.getMerchant())
                .description(request.getDescription())
                .amount(request.getAmount())
                .expenseDate(request.getExpenseDate())
                .paymentMethod(request.getPaymentMethod())
                .user(currentUser)
                .category(category)
                .build();

        Expense savedExpense = expenseRepository.saveAndFlush(expense);

        return expenseMapper.toResponse(savedExpense);

    }

    @Override
    @Transactional
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
    @Transactional
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
                                        "Category not found"
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
