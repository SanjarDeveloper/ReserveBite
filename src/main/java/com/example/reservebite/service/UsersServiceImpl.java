package com.example.reservebite.service;

import com.example.reservebite.entity.Role;
import com.example.reservebite.entity.Users;
import com.example.reservebite.repository.RoleRepository;
import com.example.reservebite.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersServiceImpl(UsersRepository usersRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.roleRepository = roleRepository;
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
    public Users saveUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null){
            userRole = new Role();
            userRole.setName("USER_ROLE");
            userRole.setActive(true);
            roleRepository.save(userRole);
        }
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>(Collections.singleton(userRole)));
        }
        if (user.getBalance() == null) {
            user.setBalance(BigDecimal.valueOf(0));
        }
        if(user.getIsActive() == null){
            user.setIsActive(true);
        }
        usersRepository.save(user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
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
        Users user = usersRepository.findByUsername(username);
        if (user != null){
            return true;
        }else return false;
    }

    @Override
    public boolean checkCredentials(Users users) {
        if (users != null){
            Users byUsername = usersRepository.findByUsername(users.getUsername());
            if (byUsername != null){
                if (users.getPassword().equals(byUsername.getPassword())){
                    return true;
                }
                else return false;
            }
            else return false;
        }
        else return false;
    }
}
