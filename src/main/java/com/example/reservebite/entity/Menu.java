package com.example.reservebite.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String food_name;
    private Double price;
    private Integer quantity;
    private String description;
    private boolean isActive;

    @OneToOne
    private MenuCategory menuCategory;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant_id;

    @OneToOne
    private Measurement measurement;
}
