package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.Cuisine;
import com.example.reservebite.entity.Restaurant;
import com.example.reservebite.service.CuisineService;
import com.example.reservebite.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/cuisines")
public class CuisineController {

    private final CuisineService cuisineService;
    private final RestaurantService restaurantService;

    public CuisineController(CuisineService cuisineService, RestaurantService restaurantService) {
        this.cuisineService = cuisineService;
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public String listCuisines(Model model) {
        model.addAttribute("cuisines", cuisineService.getAllCuisines());
        return "admin/cuisines/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("cuisine", new Cuisine());
        model.addAttribute("isActive", true);
        return "admin/cuisines/create";
    }

    @PostMapping
    public String createCuisine(@ModelAttribute("cuisine") Cuisine cuisine) {
        cuisineService.saveCuisine(cuisine);
        return "redirect:/admin/cuisines";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Cuisine cuisine = cuisineService.getCuisineById(id);
        model.addAttribute("cuisine", cuisine);
        return "admin/cuisines/edit";
    }

    @PostMapping("/update/{id}")
    public String updateCuisine(@PathVariable Long id, @ModelAttribute("cuisine") Cuisine cuisine) {
        cuisineService.editCuisine(id,cuisine);
        return "redirect:/admin/cuisines";
    }


    @GetMapping("/delete/{id}")
    public String deleteCuisine(@PathVariable Long id) {
        boolean incorrectDeletion = false;
        for (Restaurant allRestaurant : restaurantService.getAllRestaurants()) {
            if (allRestaurant.getCuisine().getId().equals(id)){
                incorrectDeletion = true;
            }
        }
        if (incorrectDeletion){
            return "redirect:/incorrect-cuisine-deletion-exception";
        } else {
            cuisineService.deleteCuisine(id);
            return "redirect:/admin/cuisines";
        }

    }
}
