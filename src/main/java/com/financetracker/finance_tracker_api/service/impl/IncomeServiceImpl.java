package com.financetracker.finance_tracker_api.service.impl;

import com.financetracker.finance_tracker_api.dto.request.IncomeCreateRequest;
import com.financetracker.finance_tracker_api.dto.request.IncomeFilterRequest;
import com.financetracker.finance_tracker_api.dto.request.IncomeUpdateRequest;
import com.financetracker.finance_tracker_api.dto.response.IncomeResponse;
import com.financetracker.finance_tracker_api.dto.response.PagedResponse;
import com.financetracker.finance_tracker_api.dto.response.Pagination;
import com.financetracker.finance_tracker_api.entity.Category;
import com.financetracker.finance_tracker_api.entity.Income;
import com.financetracker.finance_tracker_api.entity.User;
import com.financetracker.finance_tracker_api.exception.ResourceNotFoundException;
import com.financetracker.finance_tracker_api.mapper.IncomeMapper;
import com.financetracker.finance_tracker_api.repository.CategoryRepository;
import com.financetracker.finance_tracker_api.repository.IncomeRepository;
import com.financetracker.finance_tracker_api.service.CurrentUserService;
import com.financetracker.finance_tracker_api.service.IncomeService;
import com.financetracker.finance_tracker_api.specification.IncomeSpecificationBuilder;
import com.financetracker.finance_tracker_api.telegram.TelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;
    private final IncomeRepository incomeRepository;
    private final IncomeMapper incomeMapper;
    private final IncomeSpecificationBuilder incomeSpecificationBuilder;
    private final TelegramService telegramService;

    @Override
    @Transactional
    public IncomeResponse createIncome(
            IncomeCreateRequest request
    ) {

        User currentUser =
                currentUserService.getCurrentUser();

        return createIncome(request, currentUser);
    }

    @Override
    @Transactional
    public IncomeResponse createIncome(
            IncomeCreateRequest request,
            User user
    ) {

        Category category =
                categoryRepository
                        .findById(request.getCategoryId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found with id: " + request.getCategoryId()
                                ));

        // Build Income
        Income income = Income.builder()
                .amount(request.getAmount())
                .source(request.getSource())
                .description(request.getDescription())
                .incomeDate(request.getIncomeDate())
                .user(user)
                .category(category)
                .build();

        // Save
        Income savedIncome =
                incomeRepository.saveAndFlush(income);

        if (user.getTelegramChatId() != null) {
            telegramService.sendMessage(
                    user.getTelegramChatId(),
                    buildIncomeMessage(savedIncome)
            );
        }

        // Return Mapper
        return incomeMapper.toResponse(savedIncome);
    }

    private String buildIncomeMessage(Income income) {
        return String.format(
                "💰 *New Income Added!*\n\n" +
                        "• *Source:* %s\n" +
                        "• *Amount:* ₹%s\n" +
                        "• *Category:* %s\n" +
                        "• *Date:* %s",
                income.getSource(),
                income.getAmount(),
                income.getCategory().getName(),
                income.getIncomeDate()
        );
    }

    @Override
    @Transactional
    public PagedResponse<IncomeResponse> getAllIncomes(
            IncomeFilterRequest request,
            Pageable pageable
    ) {

        // Step 1: Get the currently logged-in user
        User currentUser = currentUserService.getCurrentUser();

        // Step 2: Build dynamic filters using Specifications
        Specification<Income> specification =
                incomeSpecificationBuilder.build(
                        request,
                        currentUser
                );

        // Step 3: Fetch paginated data from the database
        Page<Income> incomePage =
                incomeRepository.findAll(
                        specification,
                        pageable
                );

        // Step 4: Convert Entity -> Response DTO
        List<IncomeResponse> incomes =
                incomePage.getContent()
                        .stream()
                        .map(incomeMapper::toResponse)
                        .toList();

        // Step 5: Build and return the paginated response
        return PagedResponse.<IncomeResponse>builder()
                .content(incomes)
                .pagination(
                        Pagination.builder()
                                .page(incomePage.getNumber())
                                .size(incomePage.getSize())
                                .totalElements(incomePage.getTotalElements())
                                .totalPages(incomePage.getTotalPages())
                                .first(incomePage.isFirst())
                                .last(incomePage.isLast())
                                .build()
                )
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public IncomeResponse getIncomeById(UUID id) {

        User currentUser =
                currentUserService.getCurrentUser();

        Income income =
                incomeRepository
                        .findByIdAndUser(id, currentUser)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Income not found"
                                ));

        return incomeMapper.toResponse(income);
    }

    @Override
    public IncomeResponse updateIncome(UUID id, IncomeUpdateRequest request) {

        User currentUser =
                currentUserService.getCurrentUser();

        Income income =
                incomeRepository
                        .findByIdAndUser(id, currentUser)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Income not found"
                                ));
        Category category =
                categoryRepository
                        .findById(request.getCategoryId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found with id: " + request.getCategoryId()
                                ));

        income.setAmount(request.getAmount());
        income.setSource(request.getSource());
        income.setDescription(request.getDescription());
        income.setIncomeDate(request.getIncomeDate());
        income.setCategory(category);

        Income updatedIncome =
                incomeRepository.saveAndFlush(income);
        return incomeMapper.toResponse(updatedIncome);
    }

    @Override
    @Transactional
    public void deleteIncome(UUID id) {

        User currentUser =
                currentUserService.getCurrentUser();

        Income income =
                incomeRepository
                        .findByIdAndUser(
                                id,
                                currentUser
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Income not found"
                                ));

        incomeRepository.delete(income);

    }
}
