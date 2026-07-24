package com.financetracker.finance_tracker_api.controller;

import com.financetracker.finance_tracker_api.dto.request.TelegramLinkChatRequest;
import com.financetracker.finance_tracker_api.dto.response.TelegramLinkCodeResponse;
import com.financetracker.finance_tracker_api.service.TelegramLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for Telegram account linking.
 *
 * Flow:
 *   1. POST /api/v1/telegram/link-code  (authenticated)
 *      → generates a short-lived code (e.g. "A3B7K9X2"), valid 10 min
 *
 *   2. User sends "/link A3B7K9X2" to the Telegram bot
 *      → bot calls TelegramLinkService.linkChatId(code, chatId)
 *      → chatId saved to users.telegram_chat_id
 *
 *   3. Done — all bot commands now work for that user.
 */
@RestController
@RequestMapping("/api/v1/telegram")
@RequiredArgsConstructor
public class TelegramController {

    private final TelegramLinkService telegramLinkService;

    /**
     * Step 1 of linking: generate a one-time link code for the authenticated user.
     * Returns the code and its expiry time.
     * The user must send "/link <code>" to the bot within 10 minutes.
     */
    @PostMapping("/link-code")
    public ResponseEntity<TelegramLinkCodeResponse> generateLinkCode() {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(telegramLinkService.createLinkCode());
    }

    /**
     * Alternative: allow the web app/frontend to directly submit the chatId + code
     * if the user copies their chat ID manually.
     * Used by the bot internally via TelegramScheduler → TelegramLinkService.
     */
    @PostMapping("/link-chat")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void linkChat(@Valid @RequestBody TelegramLinkChatRequest request) {
        telegramLinkService.linkChatId(request.getCode(), request.getChatId());
    }
}