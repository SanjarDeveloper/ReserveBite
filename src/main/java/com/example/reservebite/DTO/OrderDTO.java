package com.example.reservebite.DTO;

import com.example.reservebite.entity.enums.FulfillmentType;

public class OrderDTO {
    private Long id;
    private String orderDate;
    private Double totalAmount;
    private String status;
    private FulfillmentType fulfillmentType;
    private String customerName;
    private String restaurantName;

    public OrderDTO(Long id, String orderDate, Double totalAmount, String status, FulfillmentType fulfillmentType, String customerName, String restaurantName) {
        this.id = id;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.fulfillmentType = fulfillmentType;
        this.customerName = customerName;
        this.restaurantName = restaurantName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public FulfillmentType getFulfillmentType() { return fulfillmentType; }
    public void setFulfillmentType(FulfillmentType fulfillmentType) { this.fulfillmentType = fulfillmentType; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
}