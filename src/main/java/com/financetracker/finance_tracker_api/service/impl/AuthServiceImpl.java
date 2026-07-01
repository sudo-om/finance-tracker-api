package com.financetracker.finance_tracker_api.service.impl;

import com.financetracker.finance_tracker_api.dto.request.LoginRequest;
import com.financetracker.finance_tracker_api.dto.request.RegisterRequest;
import com.financetracker.finance_tracker_api.dto.response.AuthResponse;
import com.financetracker.finance_tracker_api.dto.response.UserResponse;
import com.financetracker.finance_tracker_api.entity.User;
import com.financetracker.finance_tracker_api.exception.EmailAlreadyExistsException;
import com.financetracker.finance_tracker_api.repository.UserRepository;
import com.financetracker.finance_tracker_api.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered.");
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        UserResponse response = UserResponse.builder()
                .id(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .currency(savedUser.getCurrency())
                .timezone(savedUser.getTimezone())
                .build();

        return AuthResponse.builder()
                .token(null)
                .user(response)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }
}
