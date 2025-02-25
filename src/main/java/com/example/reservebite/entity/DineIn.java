package com.example.reservebite.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
public class DineIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Table table;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    private Users waiterId;

    public Long getId() {
        return id;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Users getWaiterId() {
        return waiterId;
    }

    public void setWaiterId(Users waiterId) {
        this.waiterId = waiterId;
    }
}
