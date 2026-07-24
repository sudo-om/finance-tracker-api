package com.financetracker.finance_tracker_api.repository;

import com.financetracker.finance_tracker_api.entity.Budget;
import com.financetracker.finance_tracker_api.entity.Category;
import com.financetracker.finance_tracker_api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByIdAndUser(
            UUID id,
            User user
    );

    boolean existsByUserAndCategoryAndStartDateAndEndDate(
            User user,
            Category category,
            LocalDate startDate,
            LocalDate endDate
    );

    Page<Budget> findByUser(
            User user,
            Pageable pageable
    );

    boolean existsByUserAndCategoryAndStartDateAndEndDateAndIdNot(
            User user,
            Category category,
            LocalDate startDate,
            LocalDate endDate,
            UUID id
    );

    int countByUser(
            User user
    );

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"category"})
    List<Budget> findByUser(
            User user
    );

    Optional<Budget> findFirstByUserAndCategory_NameIgnoreCaseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            User user,
            String categoryName,
            LocalDate startDate,
            LocalDate endDate
    );

}
