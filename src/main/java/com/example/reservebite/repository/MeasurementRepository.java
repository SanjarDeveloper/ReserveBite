package com.example.reservebite.repository;


import com.example.reservebite.entity.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeasurementRepository extends JpaRepository<Measurement,Long> {
    List<Measurement> findTop5ByOrderByIdDesc();
}
