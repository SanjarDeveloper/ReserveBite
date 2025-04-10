package com.example.reservebite.controller.RestControllers;

import org.springframework.web.bind.annotation.*;
import com.example.reservebite.entity.Restaurant;
import com.example.reservebite.service.RestaurantService;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantRestController {
    private final RestaurantService restaurantService;

    public RestaurantRestController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public List<Restaurant> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @GetMapping("/{id}")
    public Restaurant getRestaurantById(@PathVariable Long id) {
        return restaurantService.getRestaurantById(id);
    }
}
