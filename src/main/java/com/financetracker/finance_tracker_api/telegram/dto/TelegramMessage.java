package com.financetracker.finance_tracker_api.telegram.dto;

import lombok.Data;

@Data
public class TelegramMessage {

    private Chat chat;

    private String text;

}