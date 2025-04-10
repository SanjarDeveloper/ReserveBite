package com.example.reservebite.repository;

import com.example.reservebite.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {
    List<MenuCategory> findTop5ByOrderByIdDesc();
}
