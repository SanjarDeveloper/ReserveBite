package com.example.reservebite.service;

import com.example.reservebite.entity.Users;
import com.example.reservebite.entity.Waiter;
import com.example.reservebite.repository.WaiterRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WaiterService {
    private final WaiterRepository waiterRepository;

    public WaiterService(WaiterRepository waiterRepository) {
        this.waiterRepository = waiterRepository;
    }

    public Optional<Waiter> findByUser(Users user) {
        return waiterRepository.findByUser(user);
    }

    public Waiter getWaiterById(Long id) {
        return waiterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waiter not found with id: " + id));
    }
}