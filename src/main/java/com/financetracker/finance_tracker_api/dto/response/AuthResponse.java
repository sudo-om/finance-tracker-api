package com.financetracker.finance_tracker_api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;

    private UserResponse user;

}
