package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.Customer;
import com.example.reservebite.repository.RoleRepository;
import com.example.reservebite.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final RoleRepository roleRepository;

    public CustomerController(CustomerService customerService, RoleRepository roleRepository) {
        this.customerService = customerService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());
        return "admin/customers/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("roles", roleRepository.findActiveRoles());
        return "admin/customers/create";
    }

    @PostMapping
    public String createCustomer(@ModelAttribute("customer") Customer customer) {
        customer.setRegistrationDate(LocalDateTime.now());
        customerService.saveCustomer(customer);
        return "redirect:/admin/customers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Customer customer = customerService.getCustomerByID(id);
        model.addAttribute("customer", customer);
        return "admin/customers/edit";
    }

    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable Long id, @ModelAttribute("customer") Customer customer) {
        Customer customerById = customerService.getCustomerByID(id);
        customerById.setName(customer.getName());
        customerById.setUsername(customer.getUsername());
        customerById.setPassword(customer.getPassword());
        customerById.setEmail(customer.getEmail());
        customerById.setPhone(customer.getPhone());
        customerById.setBalance(customer.getBalance());
        customerById.setIsActive(customer.getIsActive());
        customerById.setAddress(customer.getAddress());
        customerService.saveCustomer(customerById);
        return "redirect:/admin/customers";
    }


    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return "redirect:/admin/customers";
    }
}
