package com.example.UserManagementSystem.service;

import com.example.UserManagementSystem.dto.UserRequestDto;
import com.example.UserManagementSystem.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    Page<UserResponseDto> getAllUsers(Pageable pageable);
    UserResponseDto getUserById(Long id);
    UserResponseDto createUser(UserRequestDto request);
    UserResponseDto updateUser(Long id, UserRequestDto request);
    UserResponseDto getUserByUsername(String username);
    void deleteUser(Long id);
}
