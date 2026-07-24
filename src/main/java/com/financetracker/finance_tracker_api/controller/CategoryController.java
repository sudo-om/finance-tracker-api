package com.financetracker.finance_tracker_api.controller;

import com.financetracker.finance_tracker_api.dto.response.ApiResponse;
import com.financetracker.finance_tracker_api.dto.response.CategoryResponse;
import com.financetracker.finance_tracker_api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryRepository.findAll().stream()
                .map(c -> CategoryResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .type(c.getType())
                        .icon(c.getIcon())
                        .color(c.getColor())
                        .build())
                .toList();

        return ResponseEntity.ok(
                ApiResponse.<List<CategoryResponse>>builder()
                        .success(true)
                        .message("Categories fetched successfully")
                        .data(categories)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
