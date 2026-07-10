package com.project.shedrive.User;

import com.project.shedrive.Admin.AdminService;
import com.project.shedrive.Config.OTP.OtpService;
import com.project.shedrive.Customer.CustomerService;
import com.project.shedrive.Driver.DriverService;
import com.project.shedrive.Exceptions.NotAuthException;
import com.project.shedrive.Exceptions.UserNotFoundException;
import com.project.shedrive.Exceptions.UsernameAlreadyRegisteredException;
import com.project.shedrive.User.DTOs.CreateAdminRequest;
import com.project.shedrive.User.DTOs.RegisterRequest;
import com.project.shedrive.User.DTOs.UserDto;
import com.project.shedrive.User.DTOs.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final DriverService driverService;
    private final CustomerService customerService;
    private final AdminService adminService;
    private final OtpService otpService;

    // =====================================================
    // Queries
    // =====================================================

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public User findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserNotFoundException("User not found with phone: " + phoneNumber));
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    private void save(User user) {
        userRepository.save(user);
    }

    private void validateRegister(RegisterRequest request) {
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new UsernameAlreadyRegisteredException("This phone number is already in use");
        }
        if (request.getRole() == User.Role.ADMIN) {
            throw new NotAuthException("You are not allowed to register as admin");
        }
        if (request.getGender() == User.Gender.MALE) {
            throw new NotAuthException("This app is for women only");
        }
    }

    @Transactional
    public void activateUserFirstTime(User user) {
        if (Boolean.TRUE.equals(user.getIsActive())) {
            return;
        }

        user.setIsActive(true);
        userRepository.save(user);

        switch (user.getRole()) {
            case DRIVER   -> driverService.createNewDriver(user);
            case CUSTOMER -> customerService.createCustomer(user);
            case ADMIN    -> adminService.CreateAdmin(user);
        }
    }

    public UserDto register(RegisterRequest request) {
        validateRegister(request);

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(false);
        user.setRole(request.getRole());
        save(user);

        String otp = otpService.sendOtp(request.getPhoneNumber(), OtpService.OtpPurpose.REGISTER);

        // We will add the sending service later
        // smsService.send(request.getPhoneNumber(), otp);

        return userMapper.toUserDto(user);
    }


    @Transactional
    public void verifyRegistrationOtp(String phoneNumber, String code) {
        otpService.verify(phoneNumber, code, OtpService.OtpPurpose.REGISTER);
        User user = findByPhoneNumber(phoneNumber);
        activateUserFirstTime(user);
    }

    public void resendRegistrationOtp(String phoneNumber) {
        User user = findByPhoneNumber(phoneNumber);
        if (Boolean.TRUE.equals(user.getIsActive())) {
            throw new NotAuthException("Account is already active");
        }

        String otp = otpService.resendOtp(phoneNumber, OtpService.OtpPurpose.REGISTER);

        // TODO: smsService.send(phoneNumber, otp);
    }


    public void requestPasswordReset(String phoneNumber) {
        findByPhoneNumber(phoneNumber);

        String otp = otpService.sendOtp(phoneNumber, OtpService.OtpPurpose.RESET_PASSWORD);

        // TODO: smsService.send(phoneNumber, otp);
    }


    @Transactional
    public void resetPassword(String phoneNumber, String code, String newPassword) {
        otpService.verify(phoneNumber, code, OtpService.OtpPurpose.RESET_PASSWORD);

        User user = findByPhoneNumber(phoneNumber);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public UserDto me(Long userId) {
        User user = findById(userId);
        return userMapper.toUserDto(user);
    }

    @Transactional
    public User createAdmin(CreateAdminRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(true);  // he does not need to make otp process , because there another admin who will create it
        user.setRole(User.Role.ADMIN);
        save(user);
        adminService.CreateAdmin(user);
        return user;
    }


    @Transactional
    public void blockUser(Long id, String reason) {
        User user = findById(id);
        user.setIsBlocked(true);
        user.setBlockReason(reason);
        userRepository.save(user);
    }

    @Transactional
    public void unblockUser(Long id) {
        User user = findById(id);
        user.setIsBlocked(false);
        user.setBlockReason(null);
        userRepository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}