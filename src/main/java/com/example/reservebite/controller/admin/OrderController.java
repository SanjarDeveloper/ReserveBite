package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.Order;
import com.example.reservebite.service.CustomerService;
import com.example.reservebite.service.OrderService;
import com.example.reservebite.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {
    private final OrderService orderService;
    private final CustomerService customerService;
    private final RestaurantService restaurantService;

    public OrderController(OrderService orderService, CustomerService customerService, RestaurantService restaurantService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());

        return "admin/orders/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("order", new Order());
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        model.addAttribute("customers", customerService.getAllCustomers());
        return "admin/orders/create";
    }

    @PostMapping
    public String createOrder(@ModelAttribute("order") Order order) {
        orderService.saveOrder(order);
        return "redirect:/admin/orders";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderByID(id);
        model.addAttribute("order", order);
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        model.addAttribute("customers", customerService.getAllCustomers());
        return "admin/orders/edit";
    }

    @PostMapping("/update/{id}")
    public String updateOrder(@PathVariable Long id, @ModelAttribute("order") Order order) {
        Order orderByID = orderService.getOrderByID(id);
        orderByID.setOrderDate(order.getOrderDate());
        orderByID.setOrderItems(order.getOrderItems());
        orderByID.setFulfillmentType(order.getFulfillmentType());
        orderByID.setRestaurant(order.getRestaurant());
        orderByID.setTotalAmount(order.getTotalAmount());
        orderByID.setStatus(order.getStatus());
        orderByID.setCustomer(order.getCustomer());
        orderService.saveOrder(orderByID);
        return "redirect:/admin/orders";
    }


    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return "redirect:/admin/orders";
    }
}
