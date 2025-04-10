package com.example.reservebite.controller.admin;

import com.example.reservebite.DTO.TableFormDTO;
import com.example.reservebite.entity.Restaurant;
import com.example.reservebite.entity.Table;
import com.example.reservebite.service.RestaurantService;
import com.example.reservebite.service.TableService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/tables")
public class TableController {
    private final TableService tableService;
    private final RestaurantService restaurantService;

    public TableController(TableService tableService, RestaurantService restaurantService) {
        this.tableService = tableService;
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public String listTables(Model model) {
        model.addAttribute("tables", tableService.getAllTables());
        return "admin/tables/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("tableForm", new TableFormDTO());
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "admin/tables/create";
    }

    @PostMapping
    public String createTable(@ModelAttribute("tableForm") TableFormDTO tableForm, Model model) {
        if (tableForm.getRestaurantId() == null) {
            model.addAttribute("error", "Please select a restaurant.");
            model.addAttribute("restaurants", restaurantService.getAllRestaurants());
            return "admin/tables/create";
        }

        Restaurant restaurant = restaurantService.getRestaurantByID(tableForm.getRestaurantId());
        if (restaurant == null) {
            model.addAttribute("error", "Selected restaurant does not exist.");
            model.addAttribute("restaurants", restaurantService.getAllRestaurants());
            return "admin/tables/create";
        }

        Table table = new Table();
        table.setTableNumber(tableForm.getTableNumber());
        table.setCapacity(tableForm.getCapacity());
        table.setStatus(tableForm.getStatus());
        table.setRestaurant(restaurant);

        tableService.saveTable(table);
        return "redirect:/admin/tables";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Table table = tableService.getTableByID(id);
        TableFormDTO tableForm = new TableFormDTO();
        tableForm.setId(table.getId());
        tableForm.setTableNumber(table.getTableNumber());
        tableForm.setCapacity(table.getCapacity());
        tableForm.setStatus(table.getStatus());
        if (table.getRestaurant() != null) {
            tableForm.setRestaurantId(table.getRestaurant().getId());
        }
        model.addAttribute("tableForm", tableForm);
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "admin/tables/edit";
    }

    @PostMapping("/update/{id}")
    public String updateTable(@PathVariable Long id, @ModelAttribute("tableForm") TableFormDTO tableForm, Model model) {
        if (tableForm.getRestaurantId() == null) {
            model.addAttribute("error", "Please select a restaurant.");
            model.addAttribute("restaurants", restaurantService.getAllRestaurants());
            return "admin/tables/edit";
        }

        Restaurant restaurant = restaurantService.getRestaurantByID(tableForm.getRestaurantId());
        if (restaurant == null) {
            model.addAttribute("error", "Selected restaurant does not exist.");
            model.addAttribute("restaurants", restaurantService.getAllRestaurants());
            return "admin/tables/edit";
        }

        Table table = tableService.getTableByID(id);
        table.setTableNumber(tableForm.getTableNumber());
        table.setCapacity(tableForm.getCapacity());
        table.setStatus(tableForm.getStatus());
        table.setRestaurant(restaurant);

        tableService.saveTable(table);
        return "redirect:/admin/tables";
    }

    @GetMapping("/delete/{id}")
    public String deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return "redirect:/admin/tables";
    }
}