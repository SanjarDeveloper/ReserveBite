package com.example.reservebite.repository;

import com.example.reservebite.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByIsActiveTrue();

    // Count active restaurants
    @Query("SELECT COUNT(r) FROM Restaurant r WHERE r.isActive = true")
    Long countByActiveTrue();

    // Find the 10 most recent restaurants with cuisine eagerly fetched
    @Query("SELECT r FROM Restaurant r LEFT JOIN FETCH r.cuisine ORDER BY r.id DESC")
    List<Restaurant> findTop10ByOrderByIdDesc();

    // Search restaurants by name, address, or cuisine with cuisine eagerly fetched
    @Query("SELECT r FROM Restaurant r LEFT JOIN FETCH r.cuisine WHERE " +
            "(:search IS NULL OR " +
            "(r.name IS NOT NULL AND LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%'))) OR " +
            "(r.address IS NOT NULL AND LOWER(r.address) LIKE LOWER(CONCAT('%', :search, '%'))) OR " +
            "(r.cuisine IS NOT NULL AND LOWER(r.cuisine.name) LIKE LOWER(CONCAT('%', :search, '%'))))")
    List<Restaurant> findBySearch(@Param("search") String search);

    // Fetch all restaurants with cuisine eagerly fetched
    @Query("SELECT r FROM Restaurant r LEFT JOIN FETCH r.cuisine")
    List<Restaurant> findAllWithCuisine();

    List<Restaurant> findTop5ByOrderByIdDesc();

    long countByIsActiveTrue();

    @Query(value = "SELECT r.name, COUNT(o.id) as order_count, COALESCE(SUM(o.total_amount), 0) as revenue " +
            "FROM restaurant r " + // Changed "restaurants" to "restaurant"
            "LEFT JOIN orders o ON r.id = o.restaurant_id " +
            "GROUP BY r.id, r.name " +
            "ORDER BY revenue DESC " +
            "LIMIT 5", nativeQuery = true)
    List<Object[]> findTopPerformingRestaurants();
}
