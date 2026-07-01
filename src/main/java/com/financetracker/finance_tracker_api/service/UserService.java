package com.financetracker.finance_tracker_api.service;

import com.financetracker.finance_tracker_api.dto.request.RegisterRequest;
import com.financetracker.finance_tracker_api.entity.User;

public interface UserService {

    User register(RegisterRequest request);

    User findByEmail(String email);
}
