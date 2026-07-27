package com.example.UserManagementSystem.service.impl;

import com.example.UserManagementSystem.dto.UserRequestDto;
import com.example.UserManagementSystem.dto.UserResponseDto;
import com.example.UserManagementSystem.entity.User;
import com.example.UserManagementSystem.exception.UserAlreadyExistsException;
import com.example.UserManagementSystem.exception.UserNotFoundException;
import com.example.UserManagementSystem.mapper.UserMapper;
import com.example.UserManagementSystem.repository.UserRepository;
import com.example.UserManagementSystem.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Profile({"h2", "default"})
@RequiredArgsConstructor
@Slf4j
@Transactional
public class H2UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        log.info("[H2 Storage Service] Activated and initialized successfully.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        log.info("[H2 Storage] Fetching all users from H2 Database.");
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        log.info("[H2 Storage] Fetching user by ID: {} from H2 Database.", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponseDto createUser(UserRequestDto request) {
        log.info("[H2 Storage] Attempting to create user: {} in H2 Database.", request.getUsername());
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email ID already exists.");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        User savedUser = userRepository.save(user);
        log.info("[H2 Storage] User created successfully with ID: {} in H2 Database.", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto request) {
        log.info("[H2 Storage] Attempting to update user with ID: {} in H2 Database.", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (userRepository.existsByUsernameAndIdNot(request.getUsername(), id)) {
            throw new UserAlreadyExistsException("Username already exists.");
        }
        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new UserAlreadyExistsException("Email ID already exists.");
        }

        userMapper.updateEntity(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        User updatedUser = userRepository.save(user);
        log.info("[H2 Storage] User with ID: {} updated successfully in H2 Database.", updatedUser.getId());
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByUsername(String username) {
        log.info("[H2 Storage] Fetching user by username: {} from H2 Database.", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        return userMapper.toResponse(user);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("[H2 Storage] Attempting to delete user with ID: {} from H2 Database.", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
        log.info("[H2 Storage] User with ID: {} deleted successfully from H2 Database.", id);
    }
}
