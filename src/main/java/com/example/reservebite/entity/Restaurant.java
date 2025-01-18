package com.example.reservebite.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private Integer totalTables;
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "cuisine_id")
    private Cuisine cuisine;

    @OneToMany(mappedBy = "restaurant_id", cascade = CascadeType.ALL)
    private List<Menu> menus;

}

