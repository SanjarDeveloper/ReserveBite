package com.example.reservebite.repository;

import com.example.reservebite.entity.Users;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByUsername(String username);

    @Query("SELECT u FROM Users u JOIN FETCH u.roles WHERE u.username = :username")
    Users findByUsernameWithRoles(@Param("username") String username);

    @Query("SELECT COUNT(DISTINCT o.customer.id) FROM Order o")
    long getTotalClients();
    // Count active users
    @Query("SELECT COUNT(u) FROM Users u WHERE u.isActive = true")
    Long countByIsActiveTrue();

    List<Users> findTop5ByOrderByIdDesc();

    @Query("SELECT u FROM Users u WHERE " +
            "LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.phone) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Users> findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCase(
            String search, String search1, String search2, String search3, Sort sort);

    @Query("SELECT u FROM Users u JOIN u.roles r WHERE r.name = :role")
    List<Users> findByRolesContaining(@Param("role") String role);

    // Implementation via @Query
    @Query("SELECT u FROM Users u JOIN u.roles r WHERE r.name = 'ROLE_ADMIN'")
    Optional<Users> findAdminUser();
}
