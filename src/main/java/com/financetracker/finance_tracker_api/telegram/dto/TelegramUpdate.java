package com.financetracker.finance_tracker_api.telegram.dto;

import lombok.Data;

@Data
public class TelegramUpdate {

    private Long update_id;

    private TelegramMessage message;

}