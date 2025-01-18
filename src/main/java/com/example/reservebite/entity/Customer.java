package com.example.reservebite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Customer extends Users{

    private String address;
    private LocalDateTime registrationDate;

}