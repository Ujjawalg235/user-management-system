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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Profile("postgres")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostgresUserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        log.info("[PostgreSQL Storage Service] Activated and initialized successfully.");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(Pageable pageable, String firstName, String lastName) {
        log.info("[PostgreSQL Storage] Querying database to fetch paginated users with optional search - First Name: {}, Last Name: {}.", firstName, lastName);
        return userRepository.searchNames(firstName, lastName, pageable)
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        log.info("[PostgreSQL Storage] Querying database for user ID: {}.", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponseDto createUser(UserRequestDto request) {
        log.info("[PostgreSQL Storage] Inserting user: {} into PostgreSQL.", request.getUsername());
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email ID already exists.");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        User savedUser = userRepository.save(user);
        log.info("[PostgreSQL Storage] Successfully inserted user ID: {}.", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto request) {
        log.info("[PostgreSQL Storage] Updating user ID: {} in PostgreSQL.", id);
        
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
        log.info("[PostgreSQL Storage] User ID: {} updated successfully.", updatedUser.getId());
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByUsername(String username) {
        log.info("[PostgreSQL Storage] Fetching user by username: {} from PostgreSQL.", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        return userMapper.toResponse(user);
    }

    @Override
    public void deleteUser(Long id){
        log.info("[PostgreSQL Storage] Deleting user ID: {} from PostgreSQL.", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
        log.info("[PostgreSQL Storage] User ID: {} deleted successfully.", id);
    }
}
