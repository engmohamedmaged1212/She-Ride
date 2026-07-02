package com.project.shedrive.Admin;

import com.project.shedrive.Admin.DTOs.AdminMapper;
import com.project.shedrive.User.DTOs.CreateAdminRequest;
import com.project.shedrive.User.User;
import com.project.shedrive.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final UserService userService;

    public User CreateAdmin(CreateAdminRequest CreateAdminRequest){
        return userService.createAdmin(CreateAdminRequest);
    }
    public void CreateAdmin(User user) {
        Admin admin = new Admin();
        admin.setUser(user);
        user.setIsActive(true);
        adminRepository.save(admin);
    }

}
