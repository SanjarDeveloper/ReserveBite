package com.example.reservebite.controller.admin;

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
        model.addAttribute("table", new Table());
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "admin/tables/create";
    }

    @PostMapping
    public String createTable(@ModelAttribute("table") Table table) {
        tableService.saveTable(table);
        return "redirect:/admin/tables";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Table table = tableService.getTableByID(id);
        model.addAttribute("table", table);
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "admin/tables/edit";
    }

    @PostMapping("/update/{id}")
    public String updateTable(@PathVariable Long id, @ModelAttribute("table") Table table) {
        Table tableByID = tableService.getTableByID(id);
        tableByID.setTableNumber(table.getTableNumber());
        tableByID.setStatus(table.getStatus());
        tableByID.setCapacity(table.getCapacity());
        tableByID.setRestaurant(table.getRestaurant());
        tableService.saveTable(tableByID);
        return "redirect:/admin/tables";
    }


    @GetMapping("/delete/{id}")
    public String deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return "redirect:/admin/tables";
    }
}
