package com.example.reservebite.service;

import com.example.reservebite.entity.Menu;
import com.example.reservebite.repository.MenuRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    public Menu getMenuById(Long id) {
        return menuRepository.findById(id).orElse(null);
    }

    public Menu saveMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    public void deleteMenu(Long id) {
        menuRepository.deleteById(id);
    }

    public List<Menu> getMenusByRestaurantId(Long restaurantId) {
        return menuRepository.findByRestaurantIdAndIsActive(restaurantId,true);
    }
}
