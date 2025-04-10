package com.example.reservebite.service;

import com.example.reservebite.entity.Restaurant;
import com.example.reservebite.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;


    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public void saveRestaurant(Restaurant restaurant) {
        restaurantRepository.save(restaurant);
    }

    public Restaurant getRestaurantByID(Long restaurantId) {
        return restaurantRepository.findById(restaurantId).orElseThrow(() -> new NoSuchElementException("No such restaurant"));
    }

    public void deleteRestaurant(Long restaurantId) {
        restaurantRepository.deleteById(restaurantId);
    }

    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
    }
    public List<Restaurant> getAllActiveRestaurants() {
        return restaurantRepository.findByIsActiveTrue();
    }
}
