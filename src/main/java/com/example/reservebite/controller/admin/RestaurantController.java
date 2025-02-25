package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.Restaurant;
import com.example.reservebite.service.CuisineService;
import com.example.reservebite.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final CuisineService cuisineService;

    public RestaurantController(RestaurantService restaurantService, CuisineService cuisineService) {
        this.restaurantService = restaurantService;
        this.cuisineService = cuisineService;
    }

    @GetMapping
    public String listRestaurants(Model model) {
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "admin/restaurants/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("restaurant", new Restaurant());
        model.addAttribute("cuisines", cuisineService.getAllCuisines());
        return "admin/restaurants/create";
    }

    @PostMapping
    public String createRestaurant(@ModelAttribute("restaurant") Restaurant restaurant) {
        restaurantService.saveRestaurant(restaurant);
        return "redirect:/admin/restaurants";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Restaurant restaurant = restaurantService.getRestaurantByID(id);
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("cuisines", cuisineService.getAllCuisines());
        return "admin/restaurants/edit";
    }

    @PostMapping("/update/{id}")
    public String updateRestaurant(@PathVariable Long id, @ModelAttribute("restaurant") Restaurant restaurant) {
        Restaurant restaurantByID = restaurantService.getRestaurantByID(id);
        restaurantByID.setName(restaurant.getName());
        restaurantByID.setAddress(restaurant.getAddress());
        restaurantByID.setPhone(restaurant.getPhone());
        restaurantByID.setEmail(restaurant.getEmail());
        restaurantByID.setTotalTables(restaurant.getTotalTables());
        restaurantByID.setActive(restaurant.getActive());
        restaurantByID.setCuisine(cuisineService.getCuisineById(restaurant.getCuisine().getId()));
        restaurantService.saveRestaurant(restaurantByID);
        return "redirect:/admin/restaurants";
    }


    @GetMapping("/delete/{id}")
    public String deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return "redirect:/admin/restaurants";
    }
}
