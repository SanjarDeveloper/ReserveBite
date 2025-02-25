package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.MenuCategory;
import com.example.reservebite.service.MenuCategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/menu-categories")
public class MenuCategoryController {

    private final MenuCategoryService menuCategoryService;

    public MenuCategoryController(MenuCategoryService menuCategoryService) {
        this.menuCategoryService = menuCategoryService;
    }

    @GetMapping
    public String listMenuCategories(Model model) {
        model.addAttribute("menuCategories", menuCategoryService.getAllMenuCategories());
        return "admin/menu-categories/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("menuCategory", new MenuCategory());
        model.addAttribute("isActive", true);
        return "admin/menu-categories/create";
    }

    @PostMapping
    public String createMenuCategory(@ModelAttribute("menuCategory") MenuCategory menuCategory) {
        menuCategoryService.saveMenuCategory(menuCategory);
        return "redirect:/admin/menu-categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        MenuCategory menuCategory = menuCategoryService.getMenuCategoryById(id);
        model.addAttribute("menuCategory", menuCategory);
        return "admin/menu-categories/edit";
    }

    @PostMapping("/update/{id}")
    public String updateMenuCategory(@PathVariable Long id, @ModelAttribute("menuCategory") MenuCategory menuCategory) {
        menuCategoryService.editMenuCategory(id,menuCategory);
        return "redirect:/admin/menu-categories";
    }


    @GetMapping("/delete/{id}")
    public String deleteMenuCategory(@PathVariable Long id) {
        menuCategoryService.deleteMenuCategory(id);
        return "redirect:/admin/menu-categories";
    }
}
