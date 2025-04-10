package com.example.reservebite.DTO;

import java.util.List;

public class OrderRequest {
    private Long restaurantId;
    private String restaurantName;
    private List<OrderItemRequest> items;
    private String deliveryAddress;
    private String deliveryCoordinates;
    private String contactNumber;
    private Double total;

    // Getters and setters
    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getDeliveryCoordinates() { return deliveryCoordinates; }
    public void setDeliveryCoordinates(String deliveryCoordinates) { this.deliveryCoordinates = deliveryCoordinates; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}