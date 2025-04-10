package com.example.reservebite.component;

import com.example.reservebite.entity.Role;
import com.example.reservebite.entity.Users;
import com.example.reservebite.repository.RoleRepository;
import com.example.reservebite.repository.UsersRepository;
import com.example.reservebite.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.Collections;

@Component
public class DataInitializer {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public CommandLineRunner initializeData(UsersService usersService, RoleRepository roleRepository, UsersRepository usersRepository) {
        return args -> {
            new TransactionTemplate(transactionManager).execute(status -> {
                // Initialize roles
                if (roleRepository.findByName("ROLE_USER") == null) {
                    Role userRole = new Role();
                    userRole.setName("ROLE_USER");
                    userRole.setActive(true);
                    roleRepository.save(userRole);
                }
                if (roleRepository.findByName("ROLE_COURIER") == null) {
                    Role userRole = new Role();
                    userRole.setName("ROLE_COURIER");
                    userRole.setActive(true);
                    roleRepository.save(userRole);
                }
                if (roleRepository.findByName("ROLE_WAITER") == null) {
                    Role userRole = new Role();
                    userRole.setName("ROLE_WAITER");
                    userRole.setActive(true);
                    roleRepository.save(userRole);
                }
                Role admin = roleRepository.findByName("ROLE_ADMIN");
                if (admin == null) {
                    Role adminRole = new Role();
                    adminRole.setName("ROLE_ADMIN");
                    adminRole.setActive(true);
                    admin = roleRepository.save(adminRole);
                }


                // Initialize admin user
                if (!usersService.usernameExists("admin")) {
                    Users adminUser = new Users(
                            "SuperAdmin",
                            "admin",
                            "admin", // Temporary password
                            "admin@gmail.com",
                            "+998909909090",
                            BigDecimal.ZERO,
                            true,
                            null
                    );
                    // Save user and get the encoded password
                    Users savedUser = usersService.saveUser(adminUser);
                    usersService.updatePassword(savedUser, savedUser.getPassword());
                    // Assign roles
                    usersService.assignRolesToUser(savedUser.getId(), Collections.singleton(admin.getId()));
                }
                return null;
            });
        };
    }
}