package com.example.reservebite.entity;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
public class DeliveryPerson extends Users{
    private String vehicleType;
    private String licenseNumber;

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
}
