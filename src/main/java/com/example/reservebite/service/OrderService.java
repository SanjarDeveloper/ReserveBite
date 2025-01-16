package com.example.reservebite.service;

import com.example.reservebite.entity.Order;
import com.example.reservebite.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderService {
    private final OrderRepository orderRepository;


    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }

    public void saveOrder(Order order){
        orderRepository.save(order);
    }

    public Order getOrderByID(Long orderId){
        return orderRepository.findById(orderId).orElseThrow(() -> new NoSuchElementException("No such order"));
    }

    public void deleteOrder(Long orderId){
        orderRepository.deleteById(orderId);
    }
}
