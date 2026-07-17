package com.financetracker.finance_tracker_api.service.impl;

import com.financetracker.finance_tracker_api.entity.User;
import com.financetracker.finance_tracker_api.repository.UserRepository;
import com.financetracker.finance_tracker_api.service.CurrentUserService;
import com.financetracker.finance_tracker_api.service.TelegramUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TelegramUserServiceImpl implements TelegramUserService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void linkTelegram(Long chatId) {

        User user = currentUserService.getCurrentUser();

        user.setTelegramChatId(chatId);

        userRepository.save(user);
    }
}

