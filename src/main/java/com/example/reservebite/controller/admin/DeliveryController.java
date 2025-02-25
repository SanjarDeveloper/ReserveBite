package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.Delivery;
import com.example.reservebite.service.DeliveryPersonService;
import com.example.reservebite.service.DeliveryService;
import com.example.reservebite.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/delivery-orders")
public class DeliveryController {
    private final DeliveryService deliveryService;
    private final DeliveryPersonService deliveryPersonService;
    private final OrderService orderService;

    public DeliveryController(DeliveryService deliveryService, DeliveryPersonService deliveryPersonService, OrderService orderService) {
        this.deliveryService = deliveryService;
        this.deliveryPersonService = deliveryPersonService;
        this.orderService = orderService;
    }

    @GetMapping
    public String listDeliverys(Model model) {
        model.addAttribute("deliveries", deliveryService.getAllDeliverys());
        return "admin/deliveries/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("delivery", new Delivery());
        model.addAttribute("couriers", deliveryPersonService.getAllDeliveryPersons());
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/deliveries/create";
    }

    @PostMapping
    public String createDelivery(@ModelAttribute("delivery") Delivery delivery) {
        deliveryService.saveDelivery(delivery);
        return "redirect:/admin/delivery-orders";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Delivery delivery = deliveryService.getDeliveryByID(id);
        model.addAttribute("delivery", delivery);
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("couriers", deliveryPersonService.getAllDeliveryPersons());
        return "admin/deliveries/edit";
    }

    @PostMapping("/update/{id}")
    public String updateDelivery(@PathVariable Long id, @ModelAttribute("delivery") Delivery delivery) {
        Delivery deliveryById = deliveryService.getDeliveryByID(id);
        deliveryById.setDeliveryFee(delivery.getDeliveryFee());
        deliveryById.setDeliveryAddress(delivery.getDeliveryAddress());
        deliveryById.setDeliveryTime(delivery.getDeliveryTime());
        deliveryById.setOrder(delivery.getOrder());
        deliveryById.setCourier(delivery.getCourier());
        deliveryService.saveDelivery(deliveryById);
        return "redirect:/admin/delivery-orders";
    }


    @GetMapping("/delete/{id}")
    public String deleteDelivery(@PathVariable Long id) {
        deliveryService.deleteDelivery(id);
        return "redirect:/admin/delivery-orders";
    }
}
