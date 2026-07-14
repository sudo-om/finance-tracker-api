package com.financetracker.finance_tracker_api.specification;

import com.financetracker.finance_tracker_api.entity.Expense;
import com.financetracker.finance_tracker_api.entity.User;
import com.financetracker.finance_tracker_api.entity.enums.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@RequiredArgsConstructor
public class ExpenseSpecification {


    public static Specification<Expense> hasSearch(String search) {

        return (root, query, criteriaBuilder) ->

                criteriaBuilder.or(

                        criteriaBuilder.like(

                                criteriaBuilder.lower(root.get("title")),

                                "%" + search.toLowerCase() + "%"

                        ),

                        criteriaBuilder.like(

                                criteriaBuilder.lower(root.get("description")),

                                "%" + search.toLowerCase() + "%"

                        )

                );

    }

    public static Specification<Expense> hasCategory(UUID categoryId) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("category").get("id"),
                        categoryId
                );

    }

    public static Specification<Expense> hasPaymentMethod(
            PaymentMethod paymentMethod
    ) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("paymentMethod"),
                        paymentMethod
                );

    }

    public static Specification<Expense> hasMinAmount(
            BigDecimal minAmount
    ) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("amount"),
                        minAmount
                );

    }

    public static Specification<Expense> hasMaxAmount(
            BigDecimal maxAmount
    ) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("amount"),
                        maxAmount
                );

    }

    public static Specification<Expense> hasStartDate(
            LocalDate startDate
    ) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("expenseDate"),
                        startDate
                );

    }

    public static Specification<Expense> hasEndDate(
            LocalDate endDate
    ) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("expenseDate"),
                        endDate
                );

    }

    public static Specification<Expense> belongsToUser(User user) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("user"),
                        user
                );

    }
}
