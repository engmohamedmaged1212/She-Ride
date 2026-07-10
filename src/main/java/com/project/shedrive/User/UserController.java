package com.project.shedrive.User;

import com.project.shedrive.User.DTOs.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;



    /**
     * POST /api/v1/users/register
     *
     * Request:
     * {
     *   "firstName": "Sara",
     *   "lastName": "Ahmed",
     *   "phoneNumber": "01012345678",
     *   "password": "Sara@123",
     *   "gender": "FEMALE",
     *   "dateOfBirth": "1998-05-15",
     *   "role": "CUSTOMER",
     *   "nationalIdNumber": "29805150100123"
     * }
     *
     * Response 201:
     * {
     *   "id": 10001,
     *   "firstName": "Sara",
     *   "lastName": "Ahmed",
     *   "gender": "FEMALE",
     *   "role": "CUSTOMER",
     *   "phoneNumber": "01012345678",
     *   "isActive": false,
     *   "isBlocked": false,
     *   "blockReason": null
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid RegisterRequest request) {
        UserDto dto = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * POST /api/v1/users/verify-otp
     *
     * Request:
     * {
     *   "phoneNumber": "01012345678",
     *   "code": "483920"
     * }
     *
     * Response 200:
     * { "message": "Account activated successfully" }
     *
     * Response 400 (wrong code):
     * { "message": "Invalid OTP code. 4 attempts remaining." }
     *
     * Response 410 (expired):
     * { "message": "OTP expired or not found" }
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<MessageResponse> verifyOtp(@RequestBody @Valid OtpVerifyRequest request) {
        userService.verifyRegistrationOtp(request.getPhoneNumber(), request.getCode());
        return ResponseEntity.ok(new MessageResponse("Account activated successfully"));
    }

    /**
     * POST /api/v1/users/resend-otp
     *
     * Request:
     * {
     *   "phoneNumber": "01012345678"
     * }
     *
     * Response 200:
     * { "message": "OTP sent successfully" }
     *
     * Response 400 (cooldown):
     * { "message": "Please wait 45 seconds before requesting another OTP." }
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<MessageResponse> resendOtp(@RequestBody @Valid PhoneRequest request) {
        userService.resendRegistrationOtp(request.getPhoneNumber());
        return ResponseEntity.ok(new MessageResponse("OTP sent successfully"));
    }



    /**
     * POST /api/v1/users/forgot-password
     *
     * Request:
     * {
     *   "phoneNumber": "01012345678"
     * }
     *
     * Response 200:
     * { "message": "OTP sent successfully" }
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestBody @Valid PhoneRequest request) {
        userService.requestPasswordReset(request.getPhoneNumber());
        return ResponseEntity.ok(new MessageResponse("OTP sent successfully"));
    }

    /**
     * POST /api/v1/users/reset-password
     *
     * Request:
     * {
     *   "phoneNumber": "01012345678",
     *   "code": "192837",
     *   "newPassword": "NewPass@456"
     * }
     *
     * Response 200:
     * { "message": "Password reset successfully" }
     */
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        userService.resetPassword(request.getPhoneNumber(), request.getCode(), request.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Password reset successfully"));
    }

    /**
     * GET /api/v1/users/me
     * Header: Authorization: Bearer {token}
     *
     * Response 200:
     * {
     *   "id": 10001,
     *   "firstName": "Sara",
     *   "lastName": "Ahmed",
     *   "gender": "FEMALE",
     *   "role": "CUSTOMER",
     *   "phoneNumber": "01012345678",
     *   "isActive": true,
     *   "isBlocked": false,
     *   "blockReason": null
     * }
     *
     * Response 401: (no token)
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyProfile(@AuthenticationPrincipal Long userId) {
        System.out.println("Controller reached");
        System.out.println(userId);
        return ResponseEntity.ok(userService.me(userId));
    }

    // Admin Only Endpoints

    /**
     * GET /api/v1/users/{id}
     * Header: Authorization: Bearer {adminToken}
     *
     * Response 200: UserDto
     * Response 403: (not admin)
     * Response 404: (user not found)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(userMapper.toUserDto(user));
    }

    /**
     * POST /api/v1/users/admin
     * Header: Authorization: Bearer {adminToken}
     *
     * Request:
     * {
     *   "firstName": "Nour",
     *   "lastName": "Hassan",
     *   "phoneNumber": "01198765432",
     *   "password": "Admin@999",
     *   "gender": "FEMALE",
     *   "dateOfBirth": "1990-01-01"
     * }
     *
     * Response 201: UserDto (role = ADMIN, isActive = true)
     * Response 403: (not admin)
     */
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> createAdmin(@RequestBody @Valid CreateAdminRequest request) {
        User admin = userService.createAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toUserDto(admin));
    }

    /**
     * PATCH /api/v1/users/{id}/block
     * Header: Authorization: Bearer {adminToken}
     *
     * Request:
     * {
     *   "reason": "Fake gender identity"
     * }
     *
     * Response 204: (no content)
     * Response 403: (not admin)
     * Response 404: (user not found)
     */
    @PatchMapping("/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> blockUser(
            @PathVariable Long id,
            @RequestBody @Valid BlockRequest request
    ) {
        userService.blockUser(id, request.getReason());
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /api/v1/users/{id}/unblock
     * Header: Authorization: Bearer {adminToken}
     *
     * Response 204: (no content)
     * Response 403: (not admin)
     */
    @PatchMapping("/{id}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unblockUser(@PathVariable Long id) {
        userService.unblockUser(id);
        return ResponseEntity.noContent().build();
    }
    // Inner Request/Response DTOs

    @lombok.Data
    public static class PhoneRequest {
        @NotBlank(message = "Phone number is required")
        private String phoneNumber;
    }

    @lombok.Data
    public static class BlockRequest {
        @NotBlank(message = "Block reason is required")
        private String reason;
    }

    public record MessageResponse(String message) {}
}