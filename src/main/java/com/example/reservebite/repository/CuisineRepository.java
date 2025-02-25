package com.example.reservebite.repository;

import com.example.reservebite.entity.Cuisine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuisineRepository extends JpaRepository<Cuisine,Long> {
}
