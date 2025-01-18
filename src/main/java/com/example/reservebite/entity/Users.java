package com.example.reservebite.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class Users {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String balance;
    private Boolean isActive;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
