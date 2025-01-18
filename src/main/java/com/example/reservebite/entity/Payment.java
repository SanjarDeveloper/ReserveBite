package com.example.reservebite.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Payment {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paymentDateTime;
    private double amount;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
