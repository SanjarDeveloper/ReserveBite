package com.example.reservebite.repository;

import com.example.reservebite.entity.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson,Long> {
}
