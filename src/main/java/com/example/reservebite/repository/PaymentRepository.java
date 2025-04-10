package com.example.reservebite.repository;

import com.example.reservebite.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findTop5ByOrderByIdDesc();
}
