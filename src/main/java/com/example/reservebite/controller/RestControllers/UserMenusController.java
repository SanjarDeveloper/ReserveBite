package com.example.reservebite.controller.RestControllers;

import com.example.reservebite.entity.Menu;
import com.example.reservebite.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menus")
public class UserMenusController {

    private final MenuRepository menuRepository;

    @Autowired
    public UserMenusController(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @GetMapping("/get-menus")
    public ResponseEntity<List<Menu>> getMenusByRestaurant(@RequestParam Long restaurantId) {
        List<Menu> menus = menuRepository.findByRestaurantId(restaurantId);
        return ResponseEntity.ok(menus);
    }
}