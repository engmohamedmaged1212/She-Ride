package com.project.shedrive.Driver;

import com.project.shedrive.Exceptions.FalseInputData;
import com.project.shedrive.User.Status;
import com.project.shedrive.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    public Driver findDriverById(Long id) {
        return driverRepository.findById(id).orElse(null);
    }
    public boolean existsDriverById(Long id) {
        return driverRepository.existsById(id);
    }
    public void createNewDriver(User user) {
        if (user.getRole() != User.Role.DRIVER) {
            throw new FalseInputData("User is not a driver");
        }
        if (existsDriverById(user.getId())) {
            throw new FalseInputData("Driver already exists");
        }
        Driver driver = new Driver();
        driver.setUser(user);
        driver.setVerificationStatus(Status.PENDING);
        driverRepository.save(driver);
    }
}
