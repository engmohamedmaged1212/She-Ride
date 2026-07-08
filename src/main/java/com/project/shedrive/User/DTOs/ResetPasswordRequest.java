package com.project.shedrive.User.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @NotBlank(message = "OTP code is required")
    private String code;
    @NotBlank(message = "New password is required")
    @jakarta.validation.constraints.Size(min = 6, max = 25)
    private String newPassword;
}
