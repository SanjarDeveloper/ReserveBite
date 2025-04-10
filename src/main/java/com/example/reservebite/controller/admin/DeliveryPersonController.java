package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.Delivery;
import com.example.reservebite.entity.DeliveryPerson;
import com.example.reservebite.entity.Role;
import com.example.reservebite.entity.Users;
import com.example.reservebite.repository.RoleRepository;
import com.example.reservebite.service.DeliveryPersonService;
import com.example.reservebite.service.UsersService;
import com.example.reservebite.service.UsersServiceImpl;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/couriers")
public class DeliveryPersonController {
    private final DeliveryPersonService deliveryPersonService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UsersService usersService;

    public DeliveryPersonController(DeliveryPersonService deliveryPersonService, PasswordEncoder passwordEncoder, RoleRepository roleRepository, UsersService usersService) {
        this.deliveryPersonService = deliveryPersonService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.usersService = usersService;
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
    public String createDeliveryPerson(@ModelAttribute("deliveryPerson") DeliveryPerson deliveryPerson, Model model) {
        try {
        boolean exists = usersService.usernameExists(deliveryPerson.getUsername());
        if (!exists) {
            deliveryPerson.setPassword(passwordEncoder.encode(deliveryPerson.getPassword()));
            Role roleCourier = roleRepository.findByName("ROLE_COURIER");
            deliveryPerson.setRoles(Collections.singleton(roleCourier));
            deliveryPersonService.saveDeliveryPerson(deliveryPerson);
        } else throw new RuntimeException("Username is already taken");
    } catch (Exception e) {
        model.addAttribute("error", e.getMessage());
            model.addAttribute("deliveryPerson", new DeliveryPerson());
            return "admin/couriers/create";
    }
        return "redirect:/admin/couriers";
    }

    @GetMapping("/edit/{id}")
    public String showEditDeliveryPersonForm(@PathVariable Long id, Model model) {
        DeliveryPerson user = deliveryPersonService.getDeliveryPersonById(id);
        model.addAttribute("user", user);
        return "admin/couriers/edit";
    }

    @PostMapping("/update/{id}")
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
