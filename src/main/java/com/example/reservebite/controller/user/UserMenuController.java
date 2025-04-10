package com.example.reservebite.controller.user;

import com.example.reservebite.entity.Menu;
import com.example.reservebite.entity.Restaurant;
import com.example.reservebite.service.MenuService;
import com.example.reservebite.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/user/menus")
public class UserMenuController {
    private final RestaurantService restaurantService;
    private final MenuService menuService;

    public UserMenuController(RestaurantService restaurantService, MenuService menuService) {
        this.restaurantService = restaurantService;
        this.menuService = menuService;
    }

    @GetMapping
    public String showMenusPage(Model model) {
        model.addAttribute("restaurants", restaurantService.getAllActiveRestaurants());
        return "user/menus/Menus";
    }

    @GetMapping("/get-menus")
    @ResponseBody
    public List<Menu> getMenusByRestaurant(@RequestParam Long restaurantId) {
        return menuService.getMenusByRestaurantId(restaurantId);
    }
}