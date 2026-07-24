package com.financetracker.finance_tracker_api.service.impl;

import com.financetracker.finance_tracker_api.dto.request.LoginRequest;
import com.financetracker.finance_tracker_api.dto.request.RegisterRequest;
import com.financetracker.finance_tracker_api.dto.response.AuthResponse;
import com.financetracker.finance_tracker_api.dto.response.UserResponse;
import com.financetracker.finance_tracker_api.entity.User;
import com.financetracker.finance_tracker_api.mapper.UserMapper;
import com.financetracker.finance_tracker_api.security.JwtService;
import com.financetracker.finance_tracker_api.service.AuthService;
import com.financetracker.finance_tracker_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import com.financetracker.finance_tracker_api.security.CustomUserDetailsService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public AuthResponse register(RegisterRequest request) {

        User savedUser = userService.register(request);

        UserResponse response = userMapper.toResponse(savedUser);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(savedUser.getEmail());
        String jwt = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .accessToken(jwt)
                .expiresIn(86400000L)
                .user(response)
                .build();

    }

    @Override
    public AuthResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(

                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )

                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        User user = userService.findByEmail(userDetails.getUsername());

        String jwt = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .accessToken(jwt)
                .expiresIn(86400000L)
                .user(userMapper.toResponse(user))
                .build();

    }
}
