package com.financetracker.finance_tracker_api.service.impl;

import com.financetracker.finance_tracker_api.dto.request.BudgetCreateRequest;
import com.financetracker.finance_tracker_api.dto.request.BudgetUpdateRequest;
import com.financetracker.finance_tracker_api.dto.response.BudgetResponse;
import com.financetracker.finance_tracker_api.dto.response.PagedResponse;
import com.financetracker.finance_tracker_api.dto.response.Pagination;
import com.financetracker.finance_tracker_api.entity.Budget;
import com.financetracker.finance_tracker_api.entity.Category;
import com.financetracker.finance_tracker_api.entity.User;
import com.financetracker.finance_tracker_api.entity.enums.BudgetStatus;
import com.financetracker.finance_tracker_api.exception.ResourceNotFoundException;
import com.financetracker.finance_tracker_api.mapper.BudgetMapper;
import com.financetracker.finance_tracker_api.repository.BudgetRepository;
import com.financetracker.finance_tracker_api.repository.CategoryRepository;
import com.financetracker.finance_tracker_api.repository.ExpenseRepository;
import com.financetracker.finance_tracker_api.service.BudgetService;
import com.financetracker.finance_tracker_api.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;
    private final BudgetMapper budgetMapper;
    private final ExpenseRepository   expenseRepository;

    private BudgetStatus determineBudgetStatus(
            BigDecimal percentageUsed
    ) {

        if (percentageUsed.compareTo(
                BigDecimal.valueOf(100)
        ) >= 0) {

            return BudgetStatus.OVER_BUDGET;

        }

        if (percentageUsed.compareTo(
                BigDecimal.valueOf(80)
        ) >= 0) {

            return BudgetStatus.WARNING;

        }

        return BudgetStatus.ON_TRACK;

    }

    private BudgetResponse buildBudgetResponse(
            Budget budget,
            User currentUser
    ) {

        BigDecimal spent =
                expenseRepository.getTotalSpentForBudget(
                        currentUser,
                        budget.getCategory(),
                        budget.getStartDate(),
                        budget.getEndDate()
                );

        BigDecimal remaining =
                budget.getAmount().subtract(spent);

        BigDecimal percentageUsed;

        if (budget.getAmount().compareTo(BigDecimal.ZERO) == 0) {

            percentageUsed = BigDecimal.ZERO;

        } else {

            percentageUsed = spent
                    .multiply(BigDecimal.valueOf(100))
                    .divide(
                            budget.getAmount(),
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        BudgetStatus status =
                determineBudgetStatus(
                        percentageUsed
                );

        BudgetResponse response =
                budgetMapper.toResponse(
                        budget
                );

        response.setSpent(spent);
        response.setRemaining(remaining);
        response.setPercentageUsed(
                percentageUsed
        );
        response.setStatus(status);

        return response;
    }

    @Override
    @Transactional
    public BudgetResponse createBudget(
            BudgetCreateRequest request
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        Category category =
                categoryRepository.findById(
                        request.getCategoryId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found"
                        )
                );

        // Validation 1

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date"
            );
        }

        // Validation 2

        boolean exists =
                budgetRepository.existsByUserAndCategoryAndStartDateAndEndDate(
                        currentUser,
                        category,
                        request.getStartDate(),
                        request.getEndDate()
                );

        if (exists) {
            throw new IllegalArgumentException(
                    "Budget already exists for this category and date range"
            );
        }

        Budget budget =
                Budget.builder()
                        .amount(request.getAmount())
                        .startDate(request.getStartDate())
                        .endDate(request.getEndDate())
                        .user(currentUser)
                        .category(category)
                        .build();

        Budget savedBudget =
                budgetRepository.saveAndFlush(budget);

        return budgetMapper.toResponse(savedBudget);

    }

    @Override
    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(UUID id) {

        //step -1 get current user
        User currentUser =
                currentUserService.getCurrentUser();

        //step -2 find the budget
        Budget budget =
                budgetRepository.findByIdAndUser(id,currentUser
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Budget not found"
                        )
                );


        BigDecimal totalSpend =
                expenseRepository.getTotalSpentForBudget(
                        currentUser,
                        budget.getCategory(),
                        budget.getStartDate(),
                        budget.getEndDate()
                );

        //step -4 calculate remaining amount
        BigDecimal remaining = budget.getAmount().subtract(totalSpend);

       // Step 5: Calculate percentage used
        BigDecimal percentageUsed;

        if(budget.getAmount().compareTo(BigDecimal.ZERO)==0){
            percentageUsed = BigDecimal.ZERO;
        }else {
            percentageUsed = totalSpend
                    .multiply(BigDecimal.valueOf(100))
                    .divide(
                            budget.getAmount(),
                            2,
                            RoundingMode.HALF_UP
                    );
        }

//        BudgetStatus status;
//
//        if (percentageUsed.compareTo(BigDecimal.valueOf(100)) >= 0) {
//            status = BudgetStatus.OVER_BUDGET;
//        } else if (percentageUsed.compareTo(BigDecimal.valueOf(80)) >= 0) {
//            status = BudgetStatus.WARNING;
//        } else {
//            status = BudgetStatus.ON_TRACK;
//        }
//        // Step 6: Map entity to response
//        BudgetResponse response =
//                budgetMapper.toResponse(budget);
//
//        // Step 7: Set calculated fields
//        response.setSpent(totalSpend);
//        response.setRemaining(remaining);
//        response.setPercentageUsed(percentageUsed);
//        response.setStatus(status);
//
//        // Step 8: Return
//        return response;
        return buildBudgetResponse(
                budget,
                currentUser
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetResponse getCurrentBudget(
            User user,
            String categoryName
    ) {

        LocalDate today = LocalDate.now();

        Budget budget = budgetRepository
                .findFirstByUserAndCategory_NameIgnoreCaseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        user,
                        categoryName,
                        today,
                        today
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No active budget found for " + categoryName + "."
                        )
                );

        return buildBudgetResponse(budget, user);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BudgetResponse> getAllBudgets(Pageable pageable) {

        User currentUser =
                currentUserService.getCurrentUser();

        Page<Budget> budgetPage =
                budgetRepository.findByUser(
                        currentUser,
                        pageable
                );
        List<BudgetResponse> responses =
                budgetPage.getContent()
                        .stream()
                        .map(budget ->
                                buildBudgetResponse(
                                        budget,
                                        currentUser
                                )
                        )
                        .toList();

        return PagedResponse.<BudgetResponse>builder()
                .content(responses)
                .pagination(
                        Pagination.builder()
                                .page(budgetPage.getNumber())
                                .size(budgetPage.getSize())
                                .totalElements(
                                        budgetPage.getTotalElements()
                                )
                                .totalPages(
                                        budgetPage.getTotalPages()
                                )
                                .first(
                                        budgetPage.isFirst()
                                )
                                .last(
                                        budgetPage.isLast()
                                )
                                .build()
                )
                .build();
    }

    @Override
    @Transactional
    public BudgetResponse updateBudget(
            UUID id,
            BudgetUpdateRequest request
    ) {

        User currentUser = currentUserService.getCurrentUser();

        Budget budget = budgetRepository
                .findByIdAndUser(id, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Budget not found"
                        ));

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found"
                        ));

        if (request.getStartDate().isAfter(request.getEndDate())) {

            throw new IllegalArgumentException(
                    "Start date cannot be after end date"
            );

        }

        boolean exists =
                budgetRepository
                        .existsByUserAndCategoryAndStartDateAndEndDateAndIdNot(
                                currentUser,
                                category,
                                request.getStartDate(),
                                request.getEndDate(),
                                id
                        );

        if (exists) {

            throw new IllegalArgumentException(
                    "Budget already exists"
            );

        }

        budget.setAmount(request.getAmount());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setCategory(category);

        Budget updated =
                budgetRepository.saveAndFlush(budget);

        return buildBudgetResponse(
                updated,
                currentUser
        );

    }

    @Override
    @Transactional
    public void deleteBudget(
            UUID id
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        Budget budget =
                budgetRepository
                        .findByIdAndUser(
                                id,
                                currentUser
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Budget not found"
                                ));

        budgetRepository.delete(
                budget
        );

    }
}
