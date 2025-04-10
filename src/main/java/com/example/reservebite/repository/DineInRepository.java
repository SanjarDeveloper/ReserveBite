package com.example.reservebite.repository;

import com.example.reservebite.entity.DineIn;
import com.example.reservebite.entity.Order;
import com.example.reservebite.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DineInRepository extends JpaRepository<DineIn,Long> {
    List<DineIn> findTop5ByOrderByIdDesc();

    List<Order> findByWaiterAndStatus(Users waiter,String status);

    void deleteByOrderId(long id);

    Optional<DineIn> findByOrderId(long id);
}
