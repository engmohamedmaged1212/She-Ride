package com.project.shedrive.Config.Bootstrap;

import com.project.shedrive.Admin.AdminService;
import com.project.shedrive.User.User;
import com.project.shedrive.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder {
    private final AdminService adminService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminConfig adminConfig;

    @EventListener(ApplicationReadyEvent.class)
    public void createAdmin() {
        // in case there is already admin
        if (userRepository.existsByRole(User.Role.ADMIN)) {return;}
        // in case not
        User admin = new User();

        admin.setFirstName("System");
        admin.setLastName("Admin");

        admin.setPhoneNumber(adminConfig.getPhone());

        admin.setPassword(
                passwordEncoder.encode(adminConfig.getPassword())
        );

        admin.setRole(User.Role.ADMIN);
        admin.setGender(User.Gender.MALE);
        admin.setIsActive(true);

        userRepository.save(admin);
        adminService.CreateAdmin(admin);
        System.out.println("Admin created successfully");
    }
}