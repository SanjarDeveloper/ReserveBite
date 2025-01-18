package com.example.reservebite.entity;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class DeliveryPerson extends Users{
    private String vehicleType;
    private String licenseNumber;

}
