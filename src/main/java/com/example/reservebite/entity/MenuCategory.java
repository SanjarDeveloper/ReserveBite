package com.example.reservebite.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MenuCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Boolean isActive;
}
