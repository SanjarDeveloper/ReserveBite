package com.example.reservebite.service;

import com.example.reservebite.entity.*;
import com.example.reservebite.repository.CartRepository;
import com.example.reservebite.repository.MenuRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final MenuRepository menuRepository;

    public CartService(CartRepository cartRepository, MenuRepository menuRepository) {
        this.cartRepository = cartRepository;
        this.menuRepository = menuRepository;
    }

    @Transactional
    public void addToCart(Users user, Long menuId, Integer quantity) {
        Cart cart = cartRepository.findByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
        }

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        CartItem item = new CartItem();
        item.setMenu(menu);
        item.setQuantity(quantity);
        cart.getItems().add(item);

        cartRepository.save(cart);
    }

    public Cart getCart(Users user) {
        return cartRepository.findByUser(user);
    }
}