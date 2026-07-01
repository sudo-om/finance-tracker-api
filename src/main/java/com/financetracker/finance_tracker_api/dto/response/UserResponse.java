package com.financetracker.finance_tracker_api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private String currency;

    private String timezone;
}
