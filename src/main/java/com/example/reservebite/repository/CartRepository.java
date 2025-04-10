package com.example.reservebite.repository;

import com.example.reservebite.entity.Cart;
import com.example.reservebite.entity.Customer;
import com.example.reservebite.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUser(Users user);
}