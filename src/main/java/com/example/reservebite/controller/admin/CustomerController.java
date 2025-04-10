package com.example.reservebite.controller.admin;

import com.example.reservebite.DTO.CustomerDTO;
import com.example.reservebite.entity.Customer;
import com.example.reservebite.entity.Users;
import com.example.reservebite.repository.RoleRepository;
import com.example.reservebite.service.CustomerService;
import com.example.reservebite.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;

@Controller
@RequestMapping("/admin/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final UsersService usersService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerController(CustomerService customerService, UsersService usersService, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.customerService = customerService;
        this.usersService = usersService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());
        return "admin/customers/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("customerDTO", new CustomerDTO());
        model.addAttribute("roles", roleRepository.findActiveRoles());
        return "admin/customers/create";
    }

    @PostMapping
    public String createCustomer(@Valid @ModelAttribute("customerDTO") CustomerDTO customerDTO,
                                 BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleRepository.findActiveRoles());
            return "admin/customers/create";
        }

        // Convert DTO to entity and save
        customerService.createCustomer(customerDTO);
        return "redirect:/admin/customers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Customer customer = customerService.getCustomerByID(id);
        model.addAttribute("customer", customer);
        model.addAttribute("roles", roleRepository.findActiveRoles());
        return "admin/customers/edit";
    }

    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable Long id, @ModelAttribute("customer") Customer customer,
                                 @RequestParam(value = "roles", required = false) Set<Long> roleIds) {
        Customer existingCustomer = customerService.getCustomerByID(id);
        Users existingUser = existingCustomer.getUser();

        // Update user fields
        existingUser.setName(customer.getUser().getName());
        existingUser.setEmail(customer.getUser().getEmail());
        existingUser.setPhone(customer.getUser().getPhone());
        String newPassword = customer.getUser().getPassword();
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            existingUser.setPassword(newPassword); // Will be encoded in saveUser
        }
        Users savedUser = usersService.saveUser(existingUser);

        // Update customer fields
        existingCustomer.setUser(savedUser);
        existingCustomer.setAddress(customer.getAddress());
        customerService.saveCustomer(existingCustomer);

        // Update roles if provided
        if (roleIds != null && !roleIds.isEmpty()) {
            customerService.assignRolesToCustomer(id, roleIds);
        }

        return "redirect:/admin/customers";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return "redirect:/admin/customers";
    }
}