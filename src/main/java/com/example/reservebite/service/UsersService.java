package com.example.reservebite.service;

import com.example.reservebite.entity.Users;

import java.util.List;
import java.util.Set;

public interface UsersService {
    List<Users> getAllUsers();
    Users getUserById(Long id);
    Users getUserByUsername(String username);
    Users saveUser(Users user);
    void deleteUser(Long id);
    void assignRolesToUser(Long userId, Set<Long> roleIds);
    boolean usernameExists(String username);
    boolean checkCredentials(Users users);
    void updatePassword(Users user, String newPassword); // Add this method
    // New method to fetch users with ROLE_USER
    public List<Users> getUsersWithRoleUser();

    public List<Users> getWaiters();
}