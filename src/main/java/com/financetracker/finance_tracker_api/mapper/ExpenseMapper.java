package com.financetracker.finance_tracker_api.mapper;

import com.financetracker.finance_tracker_api.dto.response.ExpenseResponse;
import com.financetracker.finance_tracker_api.entity.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    @Mapping(source = "category.name", target = "category")
    ExpenseResponse toResponse(Expense savedExpense);
}
