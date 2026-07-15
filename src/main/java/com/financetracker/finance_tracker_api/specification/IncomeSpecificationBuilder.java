package com.financetracker.finance_tracker_api.specification;

import com.financetracker.finance_tracker_api.dto.request.IncomeFilterRequest;
import com.financetracker.finance_tracker_api.entity.Income;
import com.financetracker.finance_tracker_api.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class IncomeSpecificationBuilder {
    public Specification<Income> build(
            IncomeFilterRequest request,
            User currentUser
    ) {

        Specification<Income> specification =
                Specification.where(
                        IncomeSpecification.belongsToUser(currentUser)
                );

        if (StringUtils.hasText(request.getSearch())) {

            specification = specification.and(
                    IncomeSpecification.hasSearch(request.getSearch())
            );

        }
        if (request.getCategoryId() != null) {

            specification = specification.and(
                    IncomeSpecification.hasCategory(
                            request.getCategoryId()
                    )
            );

        }

        if (request.getMinAmount() != null) {

            specification = specification.and(
                    IncomeSpecification.hasMinAmount(
                            request.getMinAmount()
                    )
            );
        }
        if (request.getMaxAmount() != null) {

            specification = specification.and(
                    IncomeSpecification.hasMaxAmount(
                            request.getMaxAmount()
                    )
            );

        }
        if (request.getStartDate() != null) {

            specification = specification.and(
                    IncomeSpecification.hasStartDate(
                            request.getStartDate()
                    )
            );

        }
        if (request.getEndDate() != null) {

            specification = specification.and(
                    IncomeSpecification.hasEndDate(
                            request.getEndDate()
                    )
            );

        }
        return specification;
    }
}
