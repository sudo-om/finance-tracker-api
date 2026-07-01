package com.financetracker.finance_tracker_api.repository;

import com.financetracker.finance_tracker_api.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IncomeRepository extends JpaRepository<Income, UUID> {

    List<Income> findByUserId(UUID userId);

}
