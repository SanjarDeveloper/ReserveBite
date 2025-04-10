package com.example.reservebite.repository;

import com.example.reservebite.entity.Restaurant;
import com.example.reservebite.entity.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TableRepository extends JpaRepository<Table,Long> {
    @Query("SELECT t FROM Table t WHERE t.restaurant.id = :restaurantId AND t.status = 'ACTIVE'")
    List<Table> findAvailableTablesByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query("SELECT t FROM Table t WHERE t.restaurant.id = :restaurantId")
    List<Table> findByRestaurantId(@Param("restaurantId") Long restaurantId);
    List<Table> findByRestaurant(Restaurant restaurant);
    List<Table> findTop5ByOrderByIdDesc();

    @Query("SELECT t FROM Table t WHERE t.restaurant.id = :restaurantId " +
            "AND NOT EXISTS (SELECT r FROM Reservation r WHERE r.table.id = t.id " +
            "AND r.reservationDate < :endTime AND r.reservationDate >= :startTime " +
            "AND r.status != 'CANCELLED')")
    List<Table> findAvailableTablesByRestaurantIdAndDateRange(
            @Param("restaurantId") Long restaurantId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
