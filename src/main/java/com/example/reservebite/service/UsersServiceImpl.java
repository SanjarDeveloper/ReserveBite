package com.example.reservebite.service;

import com.example.reservebite.entity.*;
import com.example.reservebite.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsersServiceImpl implements UsersService, UserDetailsService {

    private final UsersRepository usersRepository;
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReservationRepository reservationRepository;
    private final WaiterRepository waiterRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersServiceImpl(UsersRepository usersRepository, CustomerRepository customerRepository,
                            RoleRepository roleRepository, OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository, ReservationRepository reservationRepository, WaiterRepository waiterRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.customerRepository = customerRepository;
        this.roleRepository = roleRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.reservationRepository = reservationRepository;
        this.waiterRepository = waiterRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    @Override
    public Users getUserById(Long id) {
        return usersRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public Users getUserByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    @Override
    public Users saveUser(Users user) {
        // Removed password encoding from saveUser
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole.setActive(true);
            roleRepository.save(userRole);
        }
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>(Collections.singleton(userRole)));
        }
        if (user.getBalance() == null) {
            user.setBalance(BigDecimal.valueOf(0));
        }
        if (user.getIsActive() == null) {
            user.setIsActive(true); // Ensure isActive is set to true by default
        }
        return usersRepository.save(user);
    }

    // New method to update the user's password explicitly
    public void updatePassword(Users user, String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("New password cannot be null or empty");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        usersRepository.save(user);
    }

    @Override
    // New method to fetch users with ROLE_USER
    public List<Users> getUsersWithRoleUser() {
        return usersRepository.findByRolesContaining("ROLE_USER");
    }

    @Override
    public List<Users> getWaiters() {
        return usersRepository.findByRolesContaining("ROLE_WAITER");
    }

    @Override
    public void deleteUser(Long id) {
        Customer customerByUserId = customerRepository.findCustomerByUserId(id);
        if (customerByUserId != null) {
            List<Order> byCustomerId = orderRepository.findByCustomerId(customerByUserId.getId());
            if (!byCustomerId.isEmpty()) {
                for (Order order : byCustomerId) {
                    List<OrderItem> byOrderId = orderItemRepository.findByOrderId(order.getId());
                    if (byOrderId != null) {
                        for (OrderItem orderItem : byOrderId) {
                            orderItemRepository.deleteById(orderItem.getId());
                        }
                    }
                    orderRepository.deleteById(order.getId());
                }
            }
            List<Reservation> byUserId = reservationRepository.findByUserId(id);
            if (!byUserId.isEmpty()) {
                for (Reservation reservation : byUserId) {
                    reservationRepository.deleteById(reservation.getId());
                }
            }
            customerRepository.deleteById(customerByUserId.getId());
        }
        Optional<Waiter> waiter = waiterRepository.findByUserId(id);
        if (waiter.isPresent()){
            waiterRepository.delete(waiter.get());
        }
        // Remove the user's roles from the user_roles table
        Users user = usersRepository.findById(id).orElse(null);
        if (user != null) {
            user.getRoles().clear(); // Remove all roles from the user
            usersRepository.save(user); // Save the user to update the user_roles table
        }
        usersRepository.deleteById(id);
    }

    @Override
    public void assignRolesToUser(Long userId, Set<Long> roleIds) {
        Users user = getUserById(userId);
        Set<Role> roles = roleIds.stream()
                .map(roleRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        user.setRoles(roles);
        usersRepository.save(user);
    }


    @Override
    public boolean usernameExists(String username) {
        return usersRepository.findByUsername(username) != null;
    }

    @Override
    public boolean checkCredentials(Users users) {
        Users byUsername = usersRepository.findByUsername(users.getUsername());
        if (byUsername != null) {
            return passwordEncoder.matches(users.getPassword(), byUsername.getPassword());
        }
        return false;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findByUsernameWithRoles(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        System.out.println("User: " + user.getUsername());
        System.out.println("Roles: " + user.getRoles().toString());
        return user; // Return the Users entity directly as UserDetails
    }
}