package com.example.reservebite.repository;

import com.example.reservebite.entity.Delivery;
import com.example.reservebite.entity.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    // Fetch recent deliveries (top 5 by delivery time, descending)
    List<Delivery> findTop5ByOrderByDeliveryTimeDesc();

    List<Delivery> findByCourier(DeliveryPerson courier);
}
