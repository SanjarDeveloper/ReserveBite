package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.Cuisine;
import com.example.reservebite.entity.Menu;
import com.example.reservebite.entity.Restaurant;
import com.example.reservebite.entity.Table;
import com.example.reservebite.service.CuisineService;
import com.example.reservebite.service.MenuService;
import com.example.reservebite.service.RestaurantService;
import com.example.reservebite.service.TableService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/cuisines")
public class CuisineController {

    private final CuisineService cuisineService;
    private final RestaurantService restaurantService;
    private final MenuService menuService;
    private final TableService tableService;

    public CuisineController(CuisineService cuisineService, RestaurantService restaurantService, MenuService menuService, TableService tableService) {
        this.cuisineService = cuisineService;
        this.restaurantService = restaurantService;
        this.menuService = menuService;
        this.tableService = tableService;
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
        for (Restaurant allRestaurant : restaurantService.getAllRestaurants()) {
            if (allRestaurant.getCuisine().getId().equals(id)) {
                for (Menu menu : menuService.getAllMenus()){
                    if(menu.getRestaurant().getId().equals(allRestaurant.getId())){
                        menuService.deleteMenu(menu.getId());
                    }
                }
                for (Table allTable : tableService.getAllTables()) {
                    if(allTable.getRestaurant().getId().equals(allRestaurant.getId())){
                        tableService.deleteTable(allTable.getId());
                    }
                }
                restaurantService.deleteRestaurant(allRestaurant.getId());
            }
        }
        cuisineService.deleteCuisine(id);
        return "redirect:/admin/cuisines";
    }
}
