package com.example.reservebite.DTO;

public class UpsaleItem {
    private String name;
    private Double revenue;

    public UpsaleItem(String name, Double revenue) {
        this.name = name;
        this.revenue = revenue;
    }

    public String getName() { return name; }
    public Double getRevenue() { return revenue; }
}
