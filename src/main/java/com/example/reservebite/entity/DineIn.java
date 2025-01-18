package com.example.reservebite.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class DineIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Table table;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToOne
    private Users waiterId;
}
