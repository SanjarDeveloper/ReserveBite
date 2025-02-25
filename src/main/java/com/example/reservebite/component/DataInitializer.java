package com.example.reservebite.component;

import com.example.reservebite.entity.Role;
import com.example.reservebite.entity.Users;
import com.example.reservebite.repository.RoleRepository;
import com.example.reservebite.repository.UsersRepository;
import com.example.reservebite.service.UsersService;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;

@Component
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UsersRepository usersRepository;
    private final UsersService usersService;

    public DataInitializer(RoleRepository roleRepository, UsersRepository usersRepository, UsersService usersService) {
        this.roleRepository = roleRepository;
        this.usersRepository = usersRepository;
        this.usersService = usersService;
    }

    @PostConstruct
    public void init() {
        if (roleRepository.findByName("ROLE_USER") == null) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
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
        if (!usersService.usernameExists("admin")) {
            Users users = new Users(
                    "SuperAdmin",
                    "admin",
                    new BCryptPasswordEncoder().encode("admin"),
                    "admin@gmail.com",
                    "+998909909090",
                    BigDecimal.ZERO,
                    true,
                    null
            );
            Users user = usersRepository.save(users);
            usersService.assignRolesToUser(user.getId(), Collections.singleton(admin.getId()));
        }
    }
}