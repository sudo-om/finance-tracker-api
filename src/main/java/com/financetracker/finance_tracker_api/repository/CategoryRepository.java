package com.financetracker.finance_tracker_api.repository;

import com.financetracker.finance_tracker_api.entity.Category;
import com.financetracker.finance_tracker_api.entity.User;
import com.financetracker.finance_tracker_api.entity.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository
        extends JpaRepository<Category, UUID> {

    List<Category> findByType(CategoryType type);


}