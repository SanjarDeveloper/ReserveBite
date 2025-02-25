package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.Role;
import com.example.reservebite.entity.Users;
import com.example.reservebite.repository.RoleRepository;
import com.example.reservebite.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/users")
public class UsersController {
    private final UsersService usersService;
    private final RoleRepository roleRepository;

    @Autowired
    public UsersController(UsersService usersService, RoleRepository roleRepository) {
        this.usersService = usersService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String listUsers(Model model) {
        List<Users> users = usersService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users/list";
    }

    @GetMapping("/new")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new Users());
        model.addAttribute("roles", roleRepository.findActiveRoles());
//        model.addAttribute("customer", new Customer());
        return "admin/users/create";
    }

    @PostMapping
    public String createUser(@ModelAttribute("user") Users user, @RequestParam("roles") Set<Long> roleIds) {
        Users savedUser = usersService.saveUser(user);
        usersService.assignRolesToUser(savedUser.getId(), roleIds);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        Users user = usersService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/users/edit";
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") Users user, @RequestParam("roles") Set<Long> roleIds) {
        Users userById = usersService.getUserById(id);
        userById.setName(user.getName());
        userById.setUsername(user.getUsername());
        userById.setEmail(user.getEmail());
        userById.setPhone(user.getPhone());
        userById.setBalance(user.getBalance());
        userById.setIsActive(user.getIsActive());
        Set<Role> roles = roleIds.stream()
                .map(roleRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        userById.setRoles(roles);
        usersService.saveUser(userById);
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        usersService.deleteUser(id);
        return "redirect:/admin/users";
    }
}