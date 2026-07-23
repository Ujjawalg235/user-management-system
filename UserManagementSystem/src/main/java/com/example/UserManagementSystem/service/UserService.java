package com.example.UserManagementSystem.service;

import com.example.UserManagementSystem.dto.UserRequestDto;
import com.example.UserManagementSystem.dto.UserResponseDto;
import java.util.List;

public interface UserService {
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(Long id);
    UserResponseDto createUser(UserRequestDto request);
    UserResponseDto updateUser(Long id, UserRequestDto request);
    void deleteUser(Long id);
}
