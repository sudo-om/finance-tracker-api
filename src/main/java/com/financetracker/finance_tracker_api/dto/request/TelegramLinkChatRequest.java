package com.financetracker.finance_tracker_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelegramLinkChatRequest {

    @NotBlank(message = "Link code is required")
    private String code;

    @NotNull(message = "Chat ID is required")
    private Long chatId;

}
