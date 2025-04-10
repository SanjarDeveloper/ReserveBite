package com.example.reservebite.DTO;

import com.example.reservebite.projections.TopSellingItemProjection;

import java.util.List;

public class DashboardStatsDto {
    private Double totalRevenue;
    private Long totalOrders;
    private Long totalClients;
    private Long totalMenus;

    public DashboardStatsDto(Double totalRevenue, Long totalOrders, Long totalClients, Long totalMenus) {
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.totalClients = totalClients;
        this.totalMenus = totalMenus;
    }

    public Double getTotalRevenue() { return totalRevenue; }
    public Long getTotalOrders() { return totalOrders; }
    public Long getTotalClients() { return totalClients; }
    public Long getTotalMenus() { return totalMenus; }
}
