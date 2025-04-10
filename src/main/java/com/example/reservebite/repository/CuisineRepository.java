package com.example.reservebite.repository;

import com.example.reservebite.entity.Cuisine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CuisineRepository extends JpaRepository<Cuisine,Long> {
    Cuisine findByName(String name);
    List<Cuisine> findAllByIsActiveTrue();

    List<Cuisine> findTop5ByOrderByIdDesc();
}
