package com.example.reservebite.repository;

import com.example.reservebite.entity.DineIn;
import com.example.reservebite.entity.Order;
import com.example.reservebite.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DineInRepository extends JpaRepository<DineIn,Long> {
    List<DineIn> findTop5ByOrderByIdDesc();

    List<Order> findByWaiterAndStatus(Users waiter,String status);

    void deleteByOrderId(long id);

    Optional<DineIn> findByOrderId(long id);
    @Query("SELECT d FROM DineIn d JOIN FETCH d.order o JOIN FETCH o.orderItems oi JOIN FETCH oi.menu JOIN FETCH d.table JOIN FETCH d.waiter")
    List<DineIn> findAllWithOrderAndItems();
}
