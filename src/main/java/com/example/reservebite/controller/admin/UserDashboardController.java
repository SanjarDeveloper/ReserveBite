package com.example.reservebite.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserDashboardController {
    @GetMapping("/user/dashboard")
    public String userDashboard(Model model) {
        return "user/dashboard";
    }
}
