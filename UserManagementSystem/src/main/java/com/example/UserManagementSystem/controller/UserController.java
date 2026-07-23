package com.example.UserManagementSystem.controller;

import com.example.UserManagementSystem.dto.UserRequestDto;
import com.example.UserManagementSystem.dto.UserResponseDto;
import com.example.UserManagementSystem.response.ApiResponse;
import com.example.UserManagementSystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for performing CRUD operations on Users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves all users and appends HATEOAS links to each resource.")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers().stream()
                .map(this::addLink)
                .collect(Collectors.toList());

        ApiResponse<List<UserResponseDto>> response = ApiResponse.<List<UserResponseDto>>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Users retrieved successfully.")
                .data(users)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a single user by ID with HATEOAS transition links.")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
        UserResponseDto user = userService.getUserById(id);
        addLink(user);

        ApiResponse<UserResponseDto> response = ApiResponse.<UserResponseDto>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("User retrieved successfully.")
                .data(user)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create user", description = "Creates a new user in the system after validating inputs.")
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(@Valid @RequestBody UserRequestDto request) {
        UserResponseDto user = userService.createUser(request);
        addLink(user);

        ApiResponse<UserResponseDto> response = ApiResponse.<UserResponseDto>builder()
                .success(true)
                .status(HttpStatus.CREATED.value())
                .message("User created successfully.")
                .data(user)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates details of an existing user after validating inputs.")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDto request) {
        UserResponseDto user = userService.updateUser(id, request);
        addLink(user);

        ApiResponse<UserResponseDto> response = ApiResponse.<UserResponseDto>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("User updated successfully.")
                .data(user)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user from the system by ID.")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .status(204)
                .message("User deleted successfully.")
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    private UserResponseDto addLink(UserResponseDto user) {
        Map<String, String> links = new HashMap<>();
        String baseUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/users/{id}")
                .buildAndExpand(user.getId())
                .toUriString();

        links.put("self", baseUri);
        links.put("update", baseUri);
        links.put("delete", baseUri);
        user.setLinks(links);
        return user;
    }
}
