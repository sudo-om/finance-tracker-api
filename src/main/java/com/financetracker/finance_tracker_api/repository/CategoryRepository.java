package com.financetracker.finance_tracker_api.repository;

import com.financetracker.finance_tracker_api.entity.Category;
import com.financetracker.finance_tracker_api.entity.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByType(CategoryType type);

}
