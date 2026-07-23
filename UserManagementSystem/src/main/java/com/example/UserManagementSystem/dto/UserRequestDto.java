package com.example.UserManagementSystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "First Name is mandatory")
    @Size(max = 50, message = "First Name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last Name is mandatory")
    @Size(max = 50, message = "Last Name must not exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Email ID is mandatory")
    @Email(message = "Email ID should have a valid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @Pattern(regexp = "^$|^\\+?[0-9\\s\\-\\.]{7,20}$", message = "Phone number format is invalid")
    private String phoneNumber;

    @Pattern(regexp = "^(ROLE_USER|ROLE_ADMIN)$", message = "Role must be either ROLE_USER or ROLE_ADMIN")
    private String role;
}
