package com.financetracker.finance_tracker_api.service;

import com.financetracker.finance_tracker_api.dto.response.TelegramLinkCodeResponse;

public interface TelegramLinkService {

    TelegramLinkCodeResponse createLinkCode();

    void linkChatId(String code, Long chatId);

}
