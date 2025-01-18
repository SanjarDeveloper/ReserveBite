package com.example.reservebite.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Order order;
    private String deliveryAddress;
    private String deliveryTime;
    private String deliveryFee;
    @OneToOne
    private DeliveryPerson courier;


}
