package com.example.reservebite.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@jakarta.persistence.Table(name = "tables")
@Data
public class Table {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    private Integer tableNumber;
    private Integer capacity;
    private String status;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}
