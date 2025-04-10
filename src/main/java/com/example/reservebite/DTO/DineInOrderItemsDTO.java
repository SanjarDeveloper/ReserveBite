package com.example.reservebite.DTO;

import java.math.BigDecimal;

public class DineInOrderItemsDTO {
    private Long dineInId;
    private String tableNumber;
    private String waiterUsername;
    private String status;
    private Long orderId;
    private String orderDate;
    private BigDecimal totalAmount; // Changed from Double to BigDecimal
    private Long orderItemId;
    private String menuName;
    private Long quantity;
    private BigDecimal price; // Changed from Double to BigDecimal

    public DineInOrderItemsDTO(Long dineInId, String tableNumber, String waiterUsername, String status,
                               Long orderId, String orderDate, BigDecimal totalAmount,
                               Long orderItemId, String menuName, Long quantity, BigDecimal price) {
        this.dineInId = dineInId;
        this.tableNumber = tableNumber;
        this.waiterUsername = waiterUsername;
        this.status = status;
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.orderItemId = orderItemId;
        this.menuName = menuName;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public Long getDineInId() {
        return dineInId;
    }

    public void setDineInId(Long dineInId) {
        this.dineInId = dineInId;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getWaiterUsername() {
        return waiterUsername;
    }

    public void setWaiterUsername(String waiterUsername) {
        this.waiterUsername = waiterUsername;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}