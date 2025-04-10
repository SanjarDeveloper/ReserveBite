package com.example.reservebite.repository;

import com.example.reservebite.entity.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, Long> {
    // Fetch recent couriers (top 5 by ID, descending)
    List<DeliveryPerson> findTop5ByOrderByIdDesc();
    Optional<DeliveryPerson> findByUsername(String username);
    // Fetch active delivery persons
    List<DeliveryPerson> findByIsActiveTrue();
}
