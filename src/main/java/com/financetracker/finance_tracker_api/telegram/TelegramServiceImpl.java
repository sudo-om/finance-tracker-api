package com.financetracker.finance_tracker_api.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TelegramServiceImpl implements TelegramService {

    private final RestClient restClient = RestClient.create();

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${telegram.chat.id}")
    private Long chatId;

    @Override
    public void sendMessage(String message) {

        String url =
                "https://api.telegram.org/bot"
                        + token
                        + "/sendMessage";

        restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Map.of(
                                "chat_id", chatId,
                                "text", message
                        )
                )
                .retrieve()
                .toBodilessEntity();
    }
}