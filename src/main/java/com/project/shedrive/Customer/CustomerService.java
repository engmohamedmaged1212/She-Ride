package com.project.shedrive.Customer;

import com.project.shedrive.Exceptions.FalseInputData;
import com.project.shedrive.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Customer findById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }
    public boolean existsCustomerById(Long id) {
        return customerRepository.existsById(id);
    }
    public Customer createCustomer(User user) {
        if (user.getRole() != User.Role.CUSTOMER) {
            throw new FalseInputData("The user is not identified as a Customer");
        }
        if (existsCustomerById(user.getId())) {
            throw new FalseInputData("Driver already exists");
        }
        Customer customer = new Customer();
        customer.setUser(user);
        customerRepository.save(customer);
        return customer;
    }
}
