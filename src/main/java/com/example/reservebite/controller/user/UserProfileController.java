package com.example.reservebite.controller.user;

import com.example.reservebite.entity.Users;
import com.example.reservebite.repository.UsersRepository;
import com.example.reservebite.service.UsersServiceImpl; // Updated import
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/profile")
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UsersServiceImpl usersServiceImpl; // Updated type

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UsersRepository usersRepository;

    @GetMapping
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        logger.info("Fetching profile for user: {}", username);

        Users user = usersServiceImpl.getUserByUsername(username);
        if (user == null) {
            logger.warn("User not found: {}", username);
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "user/profile/profile";
    }

    @PostMapping
    public String updateProfile(@Valid @ModelAttribute("user") Users updatedUser,
                                BindingResult result,
                                Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        logger.info("Updating profile for user: {}", username);

        Users existingUser = usersServiceImpl.getUserByUsername(username);
        if (existingUser == null) {
            logger.warn("User not found during update: {}", username);
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            logger.error("Validation errors: {}", result.getAllErrors());
            model.addAttribute("user", updatedUser);
            return "user/profile/profile";
        }

        // Update fields
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone(updatedUser.getPhone());

        // Update password if provided
        String newPassword = updatedUser.getPassword();
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(newPassword));
            logger.info("Password updated for user: {}", username);
        } else {
            logger.info("Password unchanged for user: {}", username);
        }

        // Save the updated user
        usersRepository.save(existingUser); // Changed to saveUser to ensure encoding
        logger.info("User profile saved: {}", existingUser);

        model.addAttribute("user", existingUser);
        model.addAttribute("updated", true);
        return "user/profile/profile";
    }
}