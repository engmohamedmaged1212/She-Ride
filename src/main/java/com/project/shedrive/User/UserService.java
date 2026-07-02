package com.project.shedrive.User;

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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    //private final DriverService driverService;
   // private final CustomerService customerService;
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    // internal using
    private void save(User user){
        userRepository.save(user);
    }

    public User findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found with phone: " + phoneNumber));
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
        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw  new UsernameAlreadyRegisteredException("this phone number is already in use");
        }
        if (request.getRole() == User.Role.ADMIN) {
            throw new NotAuthException("You are not allowed to perform this action");
        }
        if (request.getGender() == User.Gender.MALE) {
            throw new NotAuthException("The app is suitable to girls not men bro");
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(false);
        if (request.getRole() == User.Role.DRIVER) {
            user.setRole(User.Role.DRIVER);
            save(user);
            //driverService.createNewDriver(user);  // so I ignored that because the verification using the OTP
        }
        if (request.getRole() == User.Role.CUSTOMER) {
            user.setRole(User.Role.CUSTOMER);
            save(user);
            //customerService.createCustomer(user);
        }
        return userMapper.toUserDto(user);
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

