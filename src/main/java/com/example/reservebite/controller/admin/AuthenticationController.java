package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.Users;
import com.example.reservebite.repository.RoleRepository;
import com.example.reservebite.repository.UsersRepository;
import com.example.reservebite.service.UsersService;
import com.example.reservebite.service.UsersServiceImpl;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationController {
    private final UsersService usersService;
    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;

    public AuthenticationController(@Lazy UsersService usersService, UsersRepository usersRepository, RoleRepository roleRepository) {
        this.usersService = usersService;
        this.usersRepository = usersRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new Users());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") Users user, Model model) {
        if (usersService.usernameExists(user.getUsername())) {
            model.addAttribute("usernameExists", true);
            return "auth/register";
        }
        // Save the user without encoding the password
        Users savedUser = usersService.saveUser(user);
        // Encode the password explicitly
        ((UsersServiceImpl) usersService).updatePassword(savedUser, user.getPassword());
        return "redirect:/login";
    }
}