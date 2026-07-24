package com.financetracker.finance_tracker_api.service.impl;

import com.financetracker.finance_tracker_api.dto.response.TelegramLinkCodeResponse;
import com.financetracker.finance_tracker_api.entity.TelegramLinkCode;
import com.financetracker.finance_tracker_api.entity.User;
import com.financetracker.finance_tracker_api.exception.ResourceNotFoundException;
import com.financetracker.finance_tracker_api.repository.TelegramLinkCodeRepository;
import com.financetracker.finance_tracker_api.repository.UserRepository;
import com.financetracker.finance_tracker_api.service.CurrentUserService;
import com.financetracker.finance_tracker_api.service.TelegramLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TelegramLinkServiceImpl implements TelegramLinkService {

    private static final String CODE_CHARACTERS =
            "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 8;
    private static final int CODE_VALIDITY_MINUTES = 10;

    private final TelegramLinkCodeRepository linkCodeRepository;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public TelegramLinkCodeResponse createLinkCode() {

        User user = currentUserService.getCurrentUser();
        linkCodeRepository.deleteByUser(user);

        String code = generateUniqueCode();
        LocalDateTime expiresAt =
                LocalDateTime.now().plusMinutes(CODE_VALIDITY_MINUTES);

        linkCodeRepository.save(
                TelegramLinkCode.builder()
                        .code(code)
                        .expiresAt(expiresAt)
                        .user(user)
                        .build()
        );

        return TelegramLinkCodeResponse.builder()
                .code(code)
                .expiresAt(expiresAt)
                .build();
    }

    @Override
    @Transactional
    public void linkChatId(String code, Long chatId) {

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(
                    "Invalid link code. Generate a new code in the app."
            );
        }

        TelegramLinkCode linkCode = linkCodeRepository
                .findByCode(code.trim().toUpperCase())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Link code not found. Generate a new code in the app."
                        )
                );

        if (linkCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            linkCodeRepository.delete(linkCode);
            throw new IllegalArgumentException(
                    "Link code expired. Generate a new code in the app."
            );
        }

        userRepository.findByTelegramChatId(chatId)
                .filter(user -> !user.getId().equals(linkCode.getUser().getId()))
                .ifPresent(user -> {
                    throw new IllegalArgumentException(
                            "This Telegram account is already linked to another user."
                    );
                });

        User user = linkCode.getUser();
        user.setTelegramChatId(chatId);
        userRepository.save(user);
        linkCodeRepository.delete(linkCode);
    }

    private String generateUniqueCode() {

        String code;

        do {
            StringBuilder builder = new StringBuilder(CODE_LENGTH);

            for (int index = 0; index < CODE_LENGTH; index++) {
                builder.append(
                        CODE_CHARACTERS.charAt(
                                secureRandom.nextInt(CODE_CHARACTERS.length())
                        )
                );
            }

            code = builder.toString();
        } while (linkCodeRepository.existsByCode(code));

        return code;
    }

}
