package com.project.shedrive.User.DTOs;

import com.project.shedrive.User.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 25, message = "Password must be between 6 and 25 characters")
    private String password;

    @NotNull(message = "Gender is required")
    private User.Gender gender;

    @NotNull(message = "Birthdate is required")
    private LocalDate dateOfBirth;

    @NotNull(message = "Role is required")
    private User.Role role;

    private String nationalIdNumber;
}