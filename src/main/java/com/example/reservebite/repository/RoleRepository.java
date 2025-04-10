package com.example.reservebite.repository;

import com.example.reservebite.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);

    Role findRolesByUsersId(Long id);
    @Query("SELECT r FROM Role r WHERE r.isActive = true")
    List<Role> findActiveRoles();
}
