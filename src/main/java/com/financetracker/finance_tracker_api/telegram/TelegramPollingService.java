package com.financetracker.finance_tracker_api.telegram;

import com.financetracker.finance_tracker_api.telegram.dto.TelegramUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class TelegramPollingService {

    private final RestClient restClient = RestClient.create();

    @Value("${telegram.bot.token}")
    private String token;

    private Long lastUpdateId = 0L;

    public TelegramUpdateResponse getUpdates() {

        String url =
                "https://api.telegram.org/bot"
                        + token
                        + "/getUpdates?offset="
                        + (lastUpdateId + 1);

        TelegramUpdateResponse response = restClient.get()
                .uri(url)
                .retrieve()
                .body(TelegramUpdateResponse.class);

        if (response != null &&
                response.getResult() != null &&
                !response.getResult().isEmpty()) {

            lastUpdateId = response
                    .getResult()
                    .getLast()
                    .getUpdate_id();
        }

        return response;
    }
}