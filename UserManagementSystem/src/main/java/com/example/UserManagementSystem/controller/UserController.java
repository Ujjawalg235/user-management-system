package com.example.UserManagementSystem.controller;

import com.example.UserManagementSystem.dto.UserRequestDto;
import com.example.UserManagementSystem.dto.UserResponseDto;
import com.example.UserManagementSystem.response.ApiResponse;
import com.example.UserManagementSystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management system", description = "APIs for performing CRUD operations on Users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "get all users")
    public ResponseEntity<ApiResponse<Page<UserResponseDto>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<UserResponseDto> users = userService.getAllUsers(pageable);

        ApiResponse<Page<UserResponseDto>> response = ApiResponse.<Page<UserResponseDto>>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Users fetched successfully.")
                .data(users)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get current authenticated user", description = "Verifies login credentials and retrieves current user details.")
    public ResponseEntity<ApiResponse<UserResponseDto>> getCurrentUser(Authentication authentication) {
        UserResponseDto user = userService.getUserByUsername(authentication.getName());

        ApiResponse<UserResponseDto> response = ApiResponse.<UserResponseDto>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("User authenticated successfully.")
                .data(user)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get user by ID", description = "Retrieves a single user by ID.")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
        UserResponseDto user = userService.getUserById(id);

        ApiResponse<UserResponseDto> response = ApiResponse.<UserResponseDto>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("User retrieved successfully.")
                .data(user)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create user", description = "Creates a new user in the system after validating inputs.")
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(@Valid @RequestBody UserRequestDto request) {
        UserResponseDto user = userService.createUser(request);

        ApiResponse<UserResponseDto> response = ApiResponse.<UserResponseDto>builder()
                .success(true)
                .status(HttpStatus.CREATED.value())
                .message("User created successfully.")
                .data(user)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user", description = "Updates details of an existing user after validating inputs.")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDto request) {
        UserResponseDto user = userService.updateUser(id, request);

        ApiResponse<UserResponseDto> response = ApiResponse.<UserResponseDto>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("User updated successfully.")
                .data(user)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Deletes a user from the system by ID.")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .status(HttpStatus.NO_CONTENT.value())
                .message("User deleted successfully.")
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
