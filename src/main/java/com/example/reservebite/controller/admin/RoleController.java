package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.Role;
import com.example.reservebite.service.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public String listRoles(Model model) {
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/roles/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("isActive", true);
        return "admin/roles/create";
    }

    @PostMapping
    public String createRole(@ModelAttribute("role") Role role) {
        roleService.saveRole(role);
        return "redirect:/admin/roles";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Role role = roleService.getRoleById(id);
        model.addAttribute("role", role);
        return "admin/roles/edit";
    }

    @PostMapping("/update/{id}")
    public String updateRole(@PathVariable Long id, @ModelAttribute("role") Role role) {
        roleService.editRole(id,role);
        return "redirect:/admin/roles";
    }


    @GetMapping("/delete/{id}")
    public String deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return "redirect:/admin/roles";
    }
}
