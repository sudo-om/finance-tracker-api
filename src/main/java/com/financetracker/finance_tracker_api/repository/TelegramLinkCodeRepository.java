package com.financetracker.finance_tracker_api.repository;

import com.financetracker.finance_tracker_api.entity.TelegramLinkCode;
import com.financetracker.finance_tracker_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TelegramLinkCodeRepository
        extends JpaRepository<TelegramLinkCode, UUID> {

    boolean existsByCode(String code);

    Optional<TelegramLinkCode> findByCode(String code);

    void deleteByUser(User user);

}
