package com.financetracker.finance_tracker_api.service;

import com.financetracker.finance_tracker_api.dto.request.LoginRequest;
import com.financetracker.finance_tracker_api.dto.request.RegisterRequest;
import com.financetracker.finance_tracker_api.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
