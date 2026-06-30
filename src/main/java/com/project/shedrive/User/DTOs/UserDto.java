package com.project.shedrive.User.DTOs;

import com.project.shedrive.User.User;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private User.Gender gender;
    private User.Role role;
    private String phoneNumber;
    private Boolean isActive;
    private boolean isBlocked;
    private String blockReason;
}
