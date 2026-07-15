package com.financetracker.finance_tracker_api.specification;

import com.financetracker.finance_tracker_api.entity.Income;
import com.financetracker.finance_tracker_api.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class IncomeSpecification {

    public static Specification<Income> belongsToUser(User user) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("user"),
                        user
                );

    }

    public static Specification<Income> hasSearch(String search) {
        return (root, query, criteriaBuilder) ->

                criteriaBuilder.or(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("source")),
                                "%" + search.toLowerCase() + "%"
                        ),

                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("description")),
                                "%" + search.toLowerCase() + "%"
                        )
                );
    }

    public static Specification<Income> hasCategory(UUID categoryId) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("category").get("id"),
                        categoryId
                );

    }

    public static Specification<Income> hasMinAmount(
            BigDecimal minAmount
    ) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("amount"),
                        minAmount
                );

    }

    public static Specification<Income> hasMaxAmount(
            BigDecimal maxAmount
    ) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("amount"),
                        maxAmount
                );

    }

    public static Specification<Income> hasStartDate(
            LocalDate startDate
    ) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("incomeDate"),
                        startDate
                );

    }

    public static Specification<Income> hasEndDate(
            LocalDate endDate
    ) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("incomeDate"),
                        endDate
                );

    }


}
