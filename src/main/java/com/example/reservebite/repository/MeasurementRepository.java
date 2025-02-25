package com.example.reservebite.repository;


import com.example.reservebite.entity.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasurementRepository extends JpaRepository<Measurement,Long> {
}
