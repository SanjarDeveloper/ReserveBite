package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.DineIn;
import com.example.reservebite.entity.Restaurant;
import com.example.reservebite.repository.OrderRepository;
import com.example.reservebite.repository.TableRepository;
import com.example.reservebite.repository.UsersRepository;
import com.example.reservebite.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/dinein-orders")
public class DineInController {

    private final DineInService dineInService;
    private final TableService tableService;
    private final OrderService orderService;
    private final UsersService usersService;


    public DineInController(DineInService dineInService, TableService tableService, OrderService orderService, UsersService usersService) {
        this.dineInService = dineInService;
        this.tableService = tableService;
        this.orderService = orderService;
        this.usersService = usersService;
    }

    @GetMapping
    public String listDineIns(Model model) {
        model.addAttribute("dineIns", dineInService.getAllDineIns());
        return "admin/dineins/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("dineIn", new DineIn());
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("tables", tableService.getAllTables());
        model.addAttribute("waiters", usersService.getAllUsers());
        return "admin/dineins/create";
    }

    @PostMapping
    public String createDineIn(@ModelAttribute("dineIn") DineIn dineIn) {
        dineInService.saveDineIn(dineIn);
        return "redirect:/admin/dinein-orders";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        DineIn dineIn = dineInService.getDineInByID(id);
        model.addAttribute("dineIn", dineIn);
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("tables", tableService.getAllTables());
        model.addAttribute("users", usersService.getAllUsers());
        return "admin/dineins/edit";
    }

    @PostMapping("/update/{id}")
    public String updateDineIn(@PathVariable Long id, @ModelAttribute("dineIn") DineIn dineIn) {
        DineIn dineInByID = dineInService.getDineInByID(id);
        dineInByID.setOrder(dineIn.getOrder());
        dineInByID.setTable(dineIn.getTable());
        dineInByID.setWaiterId(dineIn.getWaiterId());
        dineInService.saveDineIn(dineInByID);
        return "redirect:/admin/dinein-orders";
    }


    @GetMapping("/delete/{id}")
    public String deleteDineIn(@PathVariable Long id) {
        dineInService.deleteDineIn(id);
        return "redirect:/admin/dinein-orders";
    }

}
