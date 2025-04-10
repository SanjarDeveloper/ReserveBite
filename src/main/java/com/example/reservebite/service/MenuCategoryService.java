package com.example.reservebite.service;

import com.example.reservebite.entity.MenuCategory;
import com.example.reservebite.repository.MenuCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MenuCategoryService {
    private final MenuCategoryRepository menuCategoryRepository;

    public MenuCategoryService(MenuCategoryRepository menuCategoryRepository) {
        this.menuCategoryRepository = menuCategoryRepository;
    }

    //get all menuCategories
    public List<MenuCategory> getAllMenuCategories(){
        return menuCategoryRepository.findAll();
    }

    //get one menuCategory by ID
    public MenuCategory getMenuCategoryById(Long id) {
        return menuCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuCategory not found with id: " + id));
    }

    //Save menuCategory
    public void saveMenuCategory(MenuCategory menuCategory){
        menuCategoryRepository.save(menuCategory);
    }

    //Edit menuCategory
    public void editMenuCategory(Long id, MenuCategory menuCategory){
        MenuCategory menuCategoryById = getMenuCategoryById(id);
        menuCategoryById.setName(menuCategory.getName());
        menuCategoryById.setActive(menuCategory.isActive());
        saveMenuCategory(menuCategoryById);
    }

    //Delete menuCategory
    public void deleteMenuCategory(Long id){
        menuCategoryRepository.deleteById(id);
    }


}
