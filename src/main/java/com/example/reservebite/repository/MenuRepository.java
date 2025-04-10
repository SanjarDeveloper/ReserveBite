package com.example.reservebite.repository;

import com.example.reservebite.entity.Menu;
import com.example.reservebite.entity.Order;
import com.example.reservebite.entity.Waiter;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu,Long> {
    List<Menu> findByRestaurantIdAndIsActive(Long restaurantId, boolean isActive);

    @Query("SELECT COUNT(DISTINCT m.id) FROM Menu m") // Simplified; adjust if you have menus
    Long countDistinctMenus();
    List<Menu> findByRestaurantId(Long restaurantId);
    List<Menu> findTop5ByOrderByIdDesc();

    @Query("SELECT m FROM Menu m " +
            "WHERE LOWER(m.foodName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(m.restaurant.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(m.measurement.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(m.menuCategory.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Menu> findByFoodNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrRestaurantNameContainingIgnoreCaseOrMeasurementNameContainingIgnoreCaseOrMenuCategoryNameContainingIgnoreCase(
            @Param("search") String search1,
            @Param("search") String search2,
            @Param("search") String search3,
            @Param("search") String search4,
            @Param("search") String search5,
            Sort sort);

}
