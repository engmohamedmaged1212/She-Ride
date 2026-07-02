package com.project.shedrive.User.DTOs;

import com.project.shedrive.User.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateAdminRequest {
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @NotBlank(message = "phone number is required")
    private String phoneNumber;
    @NotBlank(message = "Password is required")
    @Size(min = 6 ,max = 25, message = "Password must be between 6 and 25 characters")
    private String password;
    @NotBlank(message = "Gender is required")
    private User.Gender gender;
    @NotBlank(message = "Birthdate is required")
    private LocalDate dateOfBirth;
    private User.Role role = User.Role.ADMIN;
    private String nationalIdNumber;
}
