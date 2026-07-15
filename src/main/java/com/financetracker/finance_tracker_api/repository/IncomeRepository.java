package com.financetracker.finance_tracker_api.repository;

import com.financetracker.finance_tracker_api.entity.Income;
import com.financetracker.finance_tracker_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IncomeRepository extends JpaRepository<Income, UUID>, JpaSpecificationExecutor<Income> {

    List<Income> findByUserId(UUID userId);

    Optional<Income> findByIdAndUser(
            UUID id,
            User user
    );

}
