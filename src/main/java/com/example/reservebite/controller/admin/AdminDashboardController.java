package com.example.reservebite.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        return "admin/dashboard";
    }
}
