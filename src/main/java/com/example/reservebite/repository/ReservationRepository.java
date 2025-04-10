package com.example.reservebite.repository;

import com.example.reservebite.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    @Query("SELECT r FROM Reservation r " +
            "LEFT JOIN FETCH r.restaurant " +
            "LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH r.table " +
            "WHERE r.id = :id")
    Reservation findByIdWithAssociations(@Param("id") Long id);
    List<Reservation> findByUserId(Long id);

    // Find reservations for a specific date (ignoring time)
    @Query("SELECT r FROM Reservation r WHERE FUNCTION('DATE', r.reservationDate) = :date")
    List<Reservation> findByReservationDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.status = :status")
    Long countByStatus(@Param("status") String status);

    List<Reservation> findTop10ByOrderByReservationDateDesc();

    @Query("SELECT r FROM Reservation r WHERE " +
            "(:search IS NULL OR LOWER(r.user.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(r.restaurant.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(r.status) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Reservation> findBySearch(@Param("search") String search);

    @Query("SELECT r FROM Reservation r WHERE r.restaurant.id = :restaurantId AND r.reservationDate = :reservationDate")
    List<Reservation> findByRestaurantIdAndReservationDate(
            @Param("restaurantId") Long restaurantId,
            @Param("reservationDate") LocalDateTime reservationDate);
}
