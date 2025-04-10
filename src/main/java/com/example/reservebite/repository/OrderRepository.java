package com.example.reservebite.repository;

import com.example.reservebite.DTO.OrderStats;
import com.example.reservebite.DTO.UpsaleItem;
import com.example.reservebite.entity.Customer;
import com.example.reservebite.entity.Order;
import com.example.reservebite.entity.Restaurant;
import com.example.reservebite.entity.Waiter;
import com.example.reservebite.entity.enums.FulfillmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.stream.Collectors;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByCustomerOrderByOrderDateDesc(Customer customer);

    @Query("SELECT SUM(o.totalAmount) FROM Order o")
    Double calculateTotalRevenue();

    @Query("SELECT m.foodName, SUM(oi.quantity * m.price) FROM Menu m JOIN OrderItem oi ON m.id = oi.menu.id GROUP BY m.id, m.foodName ORDER BY SUM(oi.quantity * m.price) DESC FETCH FIRST 10 ROWS ONLY")
    List<Object[]> findTopSellingItemsRaw();

    default List<UpsaleItem> findTopSellingItems() {
        return findTopSellingItemsRaw().stream()
                .map(row -> new UpsaleItem((String) row[0], ((Number) row[1]).doubleValue()))
                .collect(Collectors.toList());
    }
    @Query(value = "SELECT DATE(order_date) AS date, COUNT(*) AS orderCount " +
            "FROM orders " +
            "GROUP BY DATE(order_date) " +
            "ORDER BY DATE(order_date)",
            nativeQuery = true)
    List<OrderStats> findOrderStats();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'PENDING'")
    Long countPendingOrders();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'COMPLETED'")
    Long countCompletedOrders();

    @Query(value = "SELECT * FROM orders WHERE order_date >= CURRENT_DATE AND order_date < CURRENT_DATE + INTERVAL '1 day'", nativeQuery = true)
    List<Order> findOrdersMadeToday();

    List<Order> findTop10ByOrderByOrderDateDesc();

    @Query("SELECT o FROM Order o WHERE " +
            "(:search IS NULL OR LOWER(o.customer.user.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(o.restaurant.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(o.status) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Order> findBySearch(@Param("search") String search);

    // Find orders by restaurant
    List<Order> findByRestaurant(Restaurant restaurant);

    // Find top 5 restaurants by order count
    @Query("SELECT o.restaurant FROM Order o GROUP BY o.restaurant ORDER BY COUNT(o) DESC")
    List<Restaurant> findTop5RestaurantsByOrderCount();

    // Find the top 5 users by order count
    @Query(value = "SELECT c.user_id, COUNT(o.id) as order_count " +
            "FROM orders o " +
            "JOIN customer c ON o.customer_id = c.id " +
            "GROUP BY c.user_id " +
            "ORDER BY order_count DESC " +
            "LIMIT 5", nativeQuery = true)
    List<Object[]> findTop5UsersByOrderCount();

    List<Order> findTop5ByOrderByOrderDateDesc();

    @Query("SELECT o FROM Order o WHERE o.fulfillmentType = :type")
    List<Order> findByFulfillmentType(@Param("type") FulfillmentType type);

    @Query("SELECT o FROM Order o WHERE o.id NOT IN (SELECT p.order.id FROM Payment p WHERE p.order IS NOT NULL)")
    List<Order> findOrdersWithoutPayments();

    List<Order> findByWaiterAndStatus(Waiter waiter, String status);
}
