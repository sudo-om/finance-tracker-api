package com.financetracker.finance_tracker_api.mapper;

import com.financetracker.finance_tracker_api.dto.response.BudgetResponse;
import com.financetracker.finance_tracker_api.entity.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    @Mapping(source = "category.name", target = "category")
    BudgetResponse toResponse(Budget budget);

}