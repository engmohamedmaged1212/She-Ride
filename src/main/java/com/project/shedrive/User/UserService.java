package com.project.shedrive.User;

import com.project.shedrive.Admin.AdminService;
import com.project.shedrive.Config.OTP.OtpService;
import com.project.shedrive.Customer.CustomerService;
import com.project.shedrive.Driver.DriverService;
import com.project.shedrive.User.DTOs.CreateAdminRequest;
import com.project.shedrive.Exceptions.NotAuthException;
import com.project.shedrive.Exceptions.UsernameAlreadyRegisteredException;
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

    // internal using
    private void validateRegister(RegisterRequest request){
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw  new UsernameAlreadyRegisteredException("this phone number is already in use");
        }
        if (request.getRole() == User.Role.ADMIN) {
            throw new NotAuthException("You are not allowed to perform this action");
        }
        if (request.getGender() == User.Gender.MALE) {
            throw new NotAuthException("The app is suitable to girls not men bro");
        }
    }
    private void save(User user){
        userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    public User findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phoneNumber));
    }
    @Transactional
    public void activateUserFirstTime (User user) {

        if (Boolean.TRUE.equals(user.getIsActive())) {
            return;
        }

        user.setIsActive(true);
        userRepository.save(user);
        switch (user.getRole()) {
            case DRIVER -> driverService.createNewDriver(user);
            case CUSTOMER -> customerService.createCustomer(user);
            case ADMIN -> adminService.CreateAdmin(user);
        }
    }

    public User createAdmin (CreateAdminRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(false);
        save(user);
        return user;
    }

    public UserDto register(RegisterRequest request) {
        validateRegister(request);
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(false);
        user.setRole(request.getRole());
        save(user);
        String otp = otpService.generateAndStore(
                request.getPhoneNumber(),
                OtpService.OtpPurpose.REGISTER
        );
        /*
            sending the otp message but we will make it later
        */
        return userMapper.toUserDto(user);
    }
    @Transactional
    public void verifyOtp(String phoneNumber, String code) {
        otpService.verify(phoneNumber, code, OtpService.OtpPurpose.REGISTER);

        User user = findByPhoneNumber(phoneNumber);
        activateUserFirstTime(user);
    }
    @Override
    public UserDetails loadUserByUsername(String phoneNumber)
            throws UsernameNotFoundException {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"
                        ));
    }
}

