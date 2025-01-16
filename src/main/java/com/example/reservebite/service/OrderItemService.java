package com.example.reservebite.service;

import com.example.reservebite.entity.OrderItem;
import com.example.reservebite.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;


    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public List<OrderItem> getAllOrderItems(){
        return orderItemRepository.findAll();
    }

    public void saveOrderItem(OrderItem orderItem){
        orderItemRepository.save(orderItem);
    }

    public OrderItem getOrderItemByID(Long orderItemId){
        return orderItemRepository.findById(orderItemId).orElseThrow(() -> new NoSuchElementException("No such orderItem"));
    }

    public void deleteOrderItem(Long orderItemId){
        orderItemRepository.deleteById(orderItemId);
    }
}
