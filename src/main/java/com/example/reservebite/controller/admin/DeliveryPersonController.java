package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.DeliveryPerson;
import com.example.reservebite.service.DeliveryPersonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin/couriers")
public class DeliveryPersonController {
    private final DeliveryPersonService deliveryPersonService;

    public DeliveryPersonController(DeliveryPersonService deliveryPersonService) {
        this.deliveryPersonService = deliveryPersonService;
    }

    @GetMapping
    public String listDeliveryPersons(Model model) {
        List<DeliveryPerson> deliveryPersons = deliveryPersonService.getAllDeliveryPersons();
        model.addAttribute("deliveryPerson", deliveryPersons);
        return "admin/couriers/list";
    }

    @GetMapping("/new")
    public String showCreateDeliveryPersonForm(Model model) {
        model.addAttribute("deliveryPerson", new DeliveryPerson());
        return "admin/couriers/create";
    }

    @PostMapping
    public String createDeliveryPerson(@ModelAttribute("deliveryPerson") DeliveryPerson deliveryPerson) {
        deliveryPersonService.saveDeliveryPerson(deliveryPerson);
        return "redirect:/admin/couriers";
    }

    @GetMapping("/edit/{id}")
    public String showEditDeliveryPersonForm(@PathVariable Long id, Model model) {
        DeliveryPerson user = deliveryPersonService.getDeliveryPersonById(id);
        model.addAttribute("user", user);
        return "admin/couriers/edit";
    }

    @PostMapping("/{id}")
    public String updateDeliveryPerson(@PathVariable Long id, @ModelAttribute("user") DeliveryPerson user) {
        DeliveryPerson userById = deliveryPersonService.getDeliveryPersonById(id);
        userById.setName(user.getName());
        userById.setUsername(user.getUsername());
        userById.setEmail(user.getEmail());
        userById.setPhone(user.getPhone());
        userById.setVehicleType(user.getVehicleType());
        userById.setLicenseNumber(user.getLicenseNumber());
        userById.setIsActive(user.getIsActive());
        deliveryPersonService.saveDeliveryPerson(userById);
        return "redirect:/admin/couriers";
    }

    @GetMapping("/delete/{id}")
    public String deleteDeliveryPerson(@PathVariable Long id) {
        deliveryPersonService.deleteDeliveryPerson(id);
        return "redirect:/admin/couriers";
    }
}
