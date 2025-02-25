package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.Menu;
import com.example.reservebite.service.MeasurementService;
import com.example.reservebite.service.MenuCategoryService;
import com.example.reservebite.service.MenuService;
import com.example.reservebite.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/menus")
public class MenuController {

    private final MenuService menuService;
    private final RestaurantService restaurantService;
    private final MeasurementService measurementService;
    private final MenuCategoryService menuCategoryService;

    public MenuController(MenuService menuService, RestaurantService restaurantService, MeasurementService measurementService, MenuCategoryService menuCategoryService) {
        this.menuService = menuService;
        this.restaurantService = restaurantService;
        this.measurementService = measurementService;
        this.menuCategoryService = menuCategoryService;
    }

    @GetMapping
    public String listMenus(Model model) {
        List<Menu> menus = menuService.getAllMenus();
        model.addAttribute("menus", menus);
        return "admin/menus/list";
    }

    @GetMapping("/new")
    public String showCreateMenuForm(Model model) {
        model.addAttribute("menu", new Menu());
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        model.addAttribute("measurements", measurementService.getAllMeasurements());
        model.addAttribute("menuCategories", menuCategoryService.getAllMenuCategories());
        return "admin/menus/create";
    }

    @PostMapping
    public String createMenu(@ModelAttribute("menu") Menu menu) {
        menuService.saveMenu(menu);
        return "redirect:/admin/menus";
    }

    @GetMapping("/edit/{id}")
    public String showEditMenuForm(@PathVariable Long id, Model model) {
        Menu menu = menuService.getMenuById(id);
        model.addAttribute("menu", menu);
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        model.addAttribute("measurements", measurementService.getAllMeasurements());
        model.addAttribute("menuCategories", menuCategoryService.getAllMenuCategories());
        return "admin/menus/edit";
    }

    @PostMapping("/{id}")
    public String updateMenu(@PathVariable Long id, @ModelAttribute("menu") Menu menu) {
        Menu existingMenu = menuService.getMenuById(id);
        existingMenu.setFoodName(menu.getFoodName());
        existingMenu.setPrice(menu.getPrice());
        existingMenu.setQuantity(menu.getQuantity());
        existingMenu.setDescription(menu.getDescription());
        existingMenu.setIsActive(menu.getIsActive());
        existingMenu.setRestaurant(menu.getRestaurant());
        existingMenu.setMeasurement(menu.getMeasurement());
        existingMenu.setMenuCategory(menu.getMenuCategory());
        menuService.saveMenu(existingMenu);
        return "redirect:/admin/menus";
    }

    @GetMapping("/delete/{id}")
    public String deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return "redirect:/admin/menus";
    }
}
