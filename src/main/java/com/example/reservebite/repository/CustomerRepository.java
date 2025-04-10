package com.example.reservebite.repository;

import com.example.reservebite.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Customer findCustomerByUserId(Long userId);

    @Query("SELECT COUNT(DISTINCT c.id) FROM Customer c") // Simplified for this example
    Long countDistinctClients();
}
