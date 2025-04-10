package com.example.reservebite.controller.courier;

import com.example.reservebite.entity.Delivery;
import com.example.reservebite.entity.DeliveryPerson;
import com.example.reservebite.repository.DeliveryPersonRepository;
import com.example.reservebite.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/courier")
public class CourierController {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private DeliveryPersonRepository deliveryPersonRepository;

    @GetMapping("/dashboard")
    public String courierDashboard(Model model) {
        // Get the currently logged-in courier
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Username is used for login
        DeliveryPerson courier = deliveryPersonRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Courier not found"));

        // Fetch deliveries assigned to this courier
        List<Delivery> deliveries = deliveryRepository.findByCourier(courier);
        model.addAttribute("deliveries", deliveries);
        model.addAttribute("courier", courier);

        return "courier/dashboard";
    }

    @GetMapping("/delivery/{id}")
    public String viewDelivery(@PathVariable Long id, Model model) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        model.addAttribute("delivery", delivery);
        return "courier/delivery-details";
    }

    @PostMapping("/delivery/{id}/update-status")
    public String updateDeliveryStatus(@PathVariable Long id, @RequestParam String status) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        delivery.getOrder().setStatus(status); // Update the order status (e.g., "DELIVERED")
        deliveryRepository.save(delivery);
        return "redirect:/courier/dashboard";
    }
}