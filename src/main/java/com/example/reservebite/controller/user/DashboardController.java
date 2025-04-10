package com.example.reservebite.controller.user;

import com.example.reservebite.entity.Message;
import com.example.reservebite.entity.Users;
import com.example.reservebite.service.MessageService;
import com.example.reservebite.service.UsersService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class DashboardController {

    private final MessageService messageService;
    private final UsersService userService;

    public DashboardController(MessageService messageService, UsersService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        Users currentUser = userService.getUserByUsername(principal.getName());
        Users admin = userService.getUserByUsername("admin"); // Implement this method to find admin user

        // Get unread messages for the current user
        List<Message> unreadMessages = messageService.getUnreadMessages(currentUser);

        model.addAttribute("unreadMessages", unreadMessages);
        // Add other attributes you need for the dashboard

        return "user/dashboard";
    }
}
