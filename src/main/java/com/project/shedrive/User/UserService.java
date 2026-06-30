package com.project.shedrive.User;

import com.project.shedrive.Exceptions.NotAuthException;
import com.project.shedrive.Exceptions.UsernameAlreadyRegisteredException;
import com.project.shedrive.User.DTOs.RegisterRequest;
import com.project.shedrive.User.DTOs.UserDto;
import com.project.shedrive.User.DTOs.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found with phone: " + phoneNumber));
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

        return null ; // Not completed for now
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

