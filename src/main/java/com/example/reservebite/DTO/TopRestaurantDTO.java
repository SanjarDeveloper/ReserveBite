package com.example.reservebite.DTO;

public class TopRestaurantDTO {
    private Long id;
    private String name;
    private Long orderCount;
    private Double revenue;

    public TopRestaurantDTO(Long id, String name, Long orderCount, Double revenue) {
        this.id = id;
        this.name = name;
        this.orderCount = orderCount;
        this.revenue = revenue;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }
}
