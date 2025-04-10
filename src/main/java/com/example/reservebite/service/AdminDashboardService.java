package com.example.reservebite.service;

import com.example.reservebite.DTO.DashboardStatsDto;
import com.example.reservebite.DTO.OrderStats;
import com.example.reservebite.DTO.UpsaleItem;
import com.example.reservebite.repository.CustomerRepository;
import com.example.reservebite.repository.MenuRepository;
import com.example.reservebite.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminDashboardService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final MenuRepository menuRepository;

    public AdminDashboardService(OrderRepository orderRepository, CustomerRepository customerRepository, MenuRepository menuRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.menuRepository = menuRepository;
    }

    public DashboardStatsDto getDashboardStats() {
        Double totalRevenue = orderRepository.calculateTotalRevenue();
        Long totalOrders = orderRepository.count();
        Long totalClients = customerRepository.countDistinctClients();
        Long totalMenus = menuRepository.countDistinctMenus();

        return new DashboardStatsDto(
                totalRevenue != null ? totalRevenue : 0.0,
                totalOrders,
                totalClients,
                totalMenus
        );
    }

    public List<UpsaleItem> getUpsaleItems() {
        return orderRepository.findTopSellingItems();
    }

    public List<OrderStats> getOrderStats() {
        return orderRepository.findOrderStats();
    }
}
