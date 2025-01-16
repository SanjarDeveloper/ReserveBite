package com.example.reservebite.controller;

import com.example.reservebite.entity.Restaurant;
import com.example.reservebite.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public String listRestaurants(Model model) {
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "restaurants/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("restaurant", new Restaurant());
        return "restaurants/create";
    }

    @PostMapping
    public String createRestaurant(@ModelAttribute("restaurant") Restaurant restaurant) {
        restaurantService.saveRestaurant(restaurant);
        return "redirect:/restaurants";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Restaurant restaurant = restaurantService.getRestaurantByID(id);
        model.addAttribute("restaurant", restaurant);
        return "restaurants/edit";
    }

    @PostMapping("/update/{id}")
    public String updateRestaurant(@PathVariable Long id, @ModelAttribute("restaurant") Restaurant restaurant) {
        restaurant.setId(id); // Ensure the ID is set
        restaurantService.saveRestaurant(restaurant);
        return "redirect:/restaurants";
    }


    @GetMapping("/delete/{id}")
    public String deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return "redirect:/restaurants";
    }
}
