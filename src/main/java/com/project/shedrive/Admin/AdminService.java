package com.project.shedrive.Admin;

import com.project.shedrive.Admin.DTOs.AdminMapper;
import com.project.shedrive.Exceptions.FalseInputData;
import com.project.shedrive.Exceptions.UsernameAlreadyExistException;
import com.project.shedrive.User.DTOs.CreateAdminRequest;
import com.project.shedrive.User.User;
import com.project.shedrive.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;

    public void CreateAdmin(User user) {
        if (adminRepository.findById(user.getId()).isPresent()) {
            throw new UsernameAlreadyExistException("User already exists");
        }
        Admin admin = new Admin();
        admin.setUser(user);
        adminRepository.save(admin);
    }
}
