package com.example.reservebite.repository;

import com.example.reservebite.entity.Restaurant;
import com.example.reservebite.entity.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TableRepository extends JpaRepository<Table,Long> {
    @Query("SELECT t FROM Table t WHERE t.restaurant.id = :restaurantId AND t.status = 'ACTIVE'")
    List<Table> findAvailableTablesByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query("SELECT t FROM Table t WHERE t.restaurant.id = :restaurantId")
    List<Table> findByRestaurantId(@Param("restaurantId") Long restaurantId);
    List<Table> findByRestaurant(Restaurant restaurant);
    List<Table> findTop5ByOrderByIdDesc();
}
