package com.example.reservebite.DTO;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateOrderRequest {
    @NotNull(message = "Table ID is required")
    private Long tableId;

    private List<OrderItemRequest> orderItems;

    private Float total;

    // Getter and setter
    public Long getTableId() { return tableId; }
    public void setTableId(Long tableId) { this.tableId = tableId; }

    public List<OrderItemRequest> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemRequest> orderItems) {
        this.orderItems = orderItems;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }
}
