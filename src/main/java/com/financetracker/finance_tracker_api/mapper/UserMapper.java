package com.financetracker.finance_tracker_api.mapper;

import com.financetracker.finance_tracker_api.dto.response.UserResponse;
import com.financetracker.finance_tracker_api.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
}
