package com.example.reservebite.service;

import com.example.reservebite.DTO.CustomerDTO;
import com.example.reservebite.DTO.OrderRequest;
import com.example.reservebite.entity.Customer;
import com.example.reservebite.entity.Users;
import com.example.reservebite.repository.CustomerRepository;
import com.example.reservebite.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final UsersService usersService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, UsersService usersService,
                           RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.usersService = usersService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer saveCustomer(Customer customer) {
        Users user = customer.getUser();
        if (user != null && user.getPassword() != null && !user.getPassword().isEmpty()) {
            // Encode the password if it's a new user or the password has changed
            Users existingUser = user.getId() != null ? usersService.getUserById(user.getId()) : null;
            if (existingUser == null || !passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
                ((UsersServiceImpl) usersService).updatePassword(user, user.getPassword());
            }
        }
        Users savedUser = usersService.saveUser(user);
        customer.setUser(savedUser);
        return customerRepository.save(customer);
    }

    public void createCustomer(CustomerDTO customerDTO) {
        // Create User entity
        Users user = new Users();
        user.setName(customerDTO.getName());
        user.setUsername(customerDTO.getUsername());
        user.setPassword(customerDTO.getPassword()); // Raw password
        user.setEmail(customerDTO.getEmail());
        user.setPhone(customerDTO.getPhone());
        user.setBalance(BigDecimal.valueOf(customerDTO.getBalance()));
        user.setIsActive(customerDTO.isActive());

        // Save User without encoding password
        Users savedUser = usersService.saveUser(user);
        // Encode the password explicitly
        ((UsersServiceImpl) usersService).updatePassword(savedUser, customerDTO.getPassword());

        // Create Customer entity
        Customer customer = new Customer();
        customer.setUser(savedUser);
        customer.setAddress(customerDTO.getAddress());
        customer.setRegistrationDate(LocalDateTime.now());

        // Save Customer
        Customer savedCustomer = customerRepository.save(customer);

        // Assign roles
        if (customerDTO.getRoleIds() != null && !customerDTO.getRoleIds().isEmpty()) {
            usersService.assignRolesToUser(savedUser.getId(), customerDTO.getRoleIds());
        }
    }

    public Customer createCustomerFromOrderRequest(OrderRequest orderRequest, Users user) {
        Customer customer = new Customer();
        // No password encoding needed here since the user already exists
        Users savedUser = usersService.saveUser(user);
        customer.setUser(savedUser);
        customer.setAddress(orderRequest.getDeliveryAddress());
        customer.setRegistrationDate(LocalDateTime.now());
        return customerRepository.save(customer);
    }

    public Customer getCustomerByID(Long customerId) {
        return customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("No such Customer"));
    }

    public Customer getCustomerByUserID(Long userId) {
        return customerRepository.findCustomerByUserId(userId);
    }

    public boolean isCustomerExist(long id) {
        return customerRepository.findAll().stream()
                .anyMatch(customer -> customer.getUser().getId().equals(id));
    }

    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    public void assignRolesToCustomer(Long customerId, Set<Long> roleIds) {
        Customer customer = getCustomerByID(customerId);
        usersService.assignRolesToUser(customer.getUser().getId(), roleIds);
        customerRepository.save(customer);
    }
}