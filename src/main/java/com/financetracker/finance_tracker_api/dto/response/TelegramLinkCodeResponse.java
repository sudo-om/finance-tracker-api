package com.financetracker.finance_tracker_api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TelegramLinkCodeResponse {

    private String code;

    private LocalDateTime expiresAt;

}
