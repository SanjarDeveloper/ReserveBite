package com.example.reservebite.repository;

import com.example.reservebite.entity.Waiter;
import com.example.reservebite.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WaiterRepository extends JpaRepository<Waiter, Long> {
    Optional<Waiter> findByUser(Users user);
    Optional<Waiter> findByUserId(Long id);
}