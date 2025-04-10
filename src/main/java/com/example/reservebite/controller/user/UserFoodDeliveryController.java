package com.example.reservebite.controller.user;

import com.example.reservebite.entity.Restaurant;
import com.example.reservebite.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user/food-delivery")
public class UserFoodDeliveryController {
    private final RestaurantService restaurantService;

    public UserFoodDeliveryController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public String showFoodDeliveryPage(Model model) {
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "user/FoodDelivery/FoodDeliveryForm";
    }
}
