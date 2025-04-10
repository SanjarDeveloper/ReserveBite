package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.Delivery;
import com.example.reservebite.entity.DeliveryPerson;
import com.example.reservebite.entity.Order;
import com.example.reservebite.service.DeliveryPersonService;
import com.example.reservebite.service.DeliveryService;
import com.example.reservebite.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/admin/delivery-orders")
public class DeliveryController {
    private final DeliveryService deliveryService;
    private final OrderService orderService;
    private final DeliveryPersonService courierService;

    public DeliveryController(DeliveryService deliveryService, OrderService orderService, DeliveryPersonService courierService) {
        this.deliveryService = deliveryService;
        this.orderService = orderService;
        this.courierService = courierService;
    }

    // Custom binder for LocalDateTime to handle datetime-local input (optional if using @DateTimeFormat)
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text != null && !text.isEmpty()) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                        setValue(LocalDateTime.parse(text, formatter));
                    } catch (DateTimeParseException e) {
                        setValue(null);
                    }
                } else {
                    setValue(null);
                }
            }

            @Override
            public String getAsText() {
                LocalDateTime value = (LocalDateTime) getValue();
                return value != null ? value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "";
            }
        });
    }

    @GetMapping
    public String listAllDeliveries(Model model) {
        model.addAttribute("deliveries", deliveryService.getAllDeliverys()); // Fixed typo
        return "admin/deliveries/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Delivery delivery = new Delivery();
        delivery.setOrder(new Order()); // Initialize the order field
        delivery.setCourier(new DeliveryPerson()); // Initialize the courier field
        model.addAttribute("delivery", delivery);
        model.addAttribute("orders", orderService.getAllOrders()); // Filter orders
        model.addAttribute("couriers", courierService.getAllDeliveryPersons());
        return "admin/deliveries/create";
    }

    @PostMapping
    public String createDelivery(
            @RequestParam("order.id") Long orderId,
            @RequestParam("courier.id") Long courierId,
            @RequestParam("deliveryTime") LocalDateTime deliveryTime,
            Model model) {

        try {
            // Validate inputs
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID is required.");
            }
            if (courierId == null) {
                throw new IllegalArgumentException("Courier ID is required.");
            }
            if (deliveryTime == null) {
                throw new IllegalArgumentException("Delivery time is required.");
            }
            if (deliveryTime.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Delivery time cannot be in the past.");
            }

            // Create new Delivery object
            Delivery delivery = new Delivery();
            delivery.setDeliveryTime(deliveryTime);

            // Set Order and Courier
            Order order = orderService.getOrderById(orderId);
            DeliveryPerson courier = courierService.getDeliveryPersonById(courierId);

            delivery.setOrder(order);
            delivery.setCourier(courier);

            // Save the Delivery
            deliveryService.saveDelivery(delivery);
            return "redirect:/admin/delivery-orders";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("orders", orderService.getAllDeliveryOrders());
            model.addAttribute("couriers", courierService.getAllDeliveryPersons());
            model.addAttribute("delivery", new Delivery()); // Add empty delivery object for form
            return "admin/deliveries/create";
        }
    }
}
