package com.example.reservebite.service;

import com.example.reservebite.entity.Users;
import com.example.reservebite.repository.UsersRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface UsersService {
    List<Users> getAllUsers();
    Users getUserById(Long id);
    Users saveUser(Users user);
    void deleteUser(Long id);
    void assignRolesToUser(Long userId, Set<Long> roleIds);
    boolean usernameExists(String username);
    boolean checkCredentials(Users users);
}
