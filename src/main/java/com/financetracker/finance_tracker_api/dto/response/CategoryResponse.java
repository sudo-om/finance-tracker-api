package com.financetracker.finance_tracker_api.dto.response;

import com.financetracker.finance_tracker_api.entity.enums.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private UUID id;
    private String name;
    private CategoryType type;
    private String icon;
    private String color;
}
