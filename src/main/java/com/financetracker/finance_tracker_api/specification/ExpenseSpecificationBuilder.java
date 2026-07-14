package com.financetracker.finance_tracker_api.specification;

import com.financetracker.finance_tracker_api.dto.request.ExpenseFilterRequest;
import com.financetracker.finance_tracker_api.entity.Expense;
import com.financetracker.finance_tracker_api.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpenseSpecificationBuilder {

    public Specification<Expense> build(
            ExpenseFilterRequest request,
            User currentUser
    ) {

        Specification<Expense> specification =
                Specification.where(
                        ExpenseSpecification.belongsToUser(currentUser)
                );

        if (request.getSearch() != null &&
                !request.getSearch().isBlank()) {

            specification = specification.and(
                    ExpenseSpecification.hasSearch(request.getSearch())
            );

        }
        if (request.getCategoryId() != null) {

            specification = specification.and(
                    ExpenseSpecification.hasCategory(
                            request.getCategoryId()
                    )
            );

        }
        if (request.getPaymentMethod() != null) {

            specification = specification.and(
                    ExpenseSpecification.hasPaymentMethod(
                            request.getPaymentMethod()
                    )
            );

        }
        if (request.getMinAmount() != null) {

            specification = specification.and(
                    ExpenseSpecification.hasMinAmount(
                            request.getMinAmount()
                    )
            );
        }
        if (request.getMaxAmount() != null) {

            specification = specification.and(
                    ExpenseSpecification.hasMaxAmount(
                            request.getMaxAmount()
                    )
            );

        }
        if (request.getStartDate() != null) {

            specification = specification.and(
                    ExpenseSpecification.hasStartDate(
                            request.getStartDate()
                    )
            );

        }
        if (request.getEndDate() != null) {

            specification = specification.and(
                    ExpenseSpecification.hasEndDate(
                            request.getEndDate()
                    )
            );

        }
        return specification;
    }
}
