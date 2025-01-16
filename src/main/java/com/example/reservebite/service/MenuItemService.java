package com.example.reservebite.service;

import com.example.reservebite.entity.MenuItem;
import com.example.reservebite.repository.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MenuItemService {
    private final MenuItemRepository menuItemRepository;


    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> getAllMenuItems(){
        return menuItemRepository.findAll();
    }

    public void saveMenuItem(MenuItem menuItem){
        menuItemRepository.save(menuItem);
    }

    public MenuItem getMenuItemByID(Long menuItemId){
        return menuItemRepository.findById(menuItemId).orElseThrow(() -> new NoSuchElementException("No such menuItem"));
    }

    public void deleteMenuItem(Long menuItemId){
        menuItemRepository.deleteById(menuItemId);
    }
}
