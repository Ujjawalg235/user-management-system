package com.example.UserManagementSystem.mapper;

import com.example.UserManagementSystem.entity.User;
import com.example.UserManagementSystem.dto.UserRequestDto;
import com.example.UserManagementSystem.dto.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequestDto request) {
        if (request == null) {
            return null;
        }

        return User.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword()) // Will be encoded in the service layer
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole() != null ? request.getRole() : "ROLE_USER")
                .active(true)
                .build();
    }

    public UserResponseDto toResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .active(user.isActive())
                .build();
    }

    public void updateEntity(User user, UserRequestDto request) {
        if (user == null || request == null) {
            return;
        }

        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(request.getPassword()); // Will be encoded in the service layer
        }
        user.setPhoneNumber(request.getPhoneNumber());
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
    }
}
