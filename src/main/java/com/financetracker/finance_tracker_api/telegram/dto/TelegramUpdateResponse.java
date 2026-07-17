package com.financetracker.finance_tracker_api.telegram.dto;

import lombok.Data;

import java.util.List;

@Data
public class TelegramUpdateResponse {

    private boolean ok;

    private List<TelegramUpdate> result;

}