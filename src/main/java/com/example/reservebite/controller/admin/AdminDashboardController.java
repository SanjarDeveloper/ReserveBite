package com.example.reservebite.controller.admin;

import com.example.reservebite.DTO.DashboardStatsDto;
import com.example.reservebite.DTO.OrderStats;
import com.example.reservebite.DTO.UpsaleItem;
import com.example.reservebite.entity.Users;
import com.example.reservebite.service.AdminDashboardService;
import com.example.reservebite.service.MessageService;
import com.example.reservebite.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;
    private final UsersService usersService;
    private final MessageService messageService;
    private final ObjectMapper objectMapper;
    public AdminDashboardController(AdminDashboardService adminDashboardService, UsersService usersService, MessageService messageService, ObjectMapper objectMapper) {
        this.adminDashboardService = adminDashboardService;
        this.usersService = usersService;
        this.messageService = messageService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        try {
            DashboardStatsDto dashboardStats = adminDashboardService.getDashboardStats();
            List<UpsaleItem> upsaleItems = adminDashboardService.getUpsaleItems();
            List<OrderStats> orderStats = adminDashboardService.getOrderStats();

            model.addAttribute("dashboardStats", dashboardStats);
            model.addAttribute("totalRevenue", dashboardStats.getTotalRevenue());
            model.addAttribute("totalOrders", dashboardStats.getTotalOrders());
            model.addAttribute("totalClients", dashboardStats.getTotalClients());
            model.addAttribute("totalMenus", dashboardStats.getTotalMenus());
            model.addAttribute("upsaleItems", upsaleItems);
            String orderStatsJson = "[]"; // Default to empty array
            try {
                orderStatsJson = objectMapper.writeValueAsString(orderStats);
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", "Error loading order stats: " + e.getMessage());
            }
            model.addAttribute("orderStats", orderStatsJson);
            // Add unread message count
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Users admin = usersService.getUserByUsername(username);
            model.addAttribute("unreadCount", messageService.getUnreadMessageCount(admin));
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
        }
        return "admin/dashboard";
    }
}
