package com.example.reservebite.service;

import com.example.reservebite.DTO.OrderItemRequest;
import com.example.reservebite.entity.*;
import com.example.reservebite.entity.enums.FulfillmentType;
import com.example.reservebite.repository.DineInRepository;
import com.example.reservebite.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final RestaurantService restaurantService;
    private final MenuService menuService;
    private final OrderItemService orderItemService;
    private final DineInRepository dineInRepository;

    public OrderService(OrderRepository orderRepository, CartService cartService,
                        RestaurantService restaurantService, MenuService menuService, OrderItemService orderItemService, DineInRepository dineInRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.restaurantService = restaurantService;
        this.menuService = menuService;
        this.orderItemService = orderItemService;
        this.dineInRepository = dineInRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getAllDeliveryOrders() {
        return orderRepository.findByFulfillmentType(FulfillmentType.DELIVERY);
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public Order saveOrderAndReturn(Order order) {
        return orderRepository.save(order);
    }

    public Order getOrderByID(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new NoSuchElementException("No such order"));
    }

    public void deleteOrder(Long orderId) {
        orderItemService.deleteOrderItemByOrderId(orderId);
        Order orderByID = getOrderByID(orderId);
        Optional<DineIn> byOrderId = dineInRepository.findByOrderId(orderByID.getId());
        byOrderId.ifPresent(dineInRepository::delete);
        orderRepository.deleteById(orderId);
    }

    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> getOrdersByCustomer(Customer customer) {
        return orderRepository.findByCustomerOrderByOrderDateDesc(customer);
    }

    public Order createOrder(Customer customer, String deliveryAddress, String contactNumber) {
        Cart cart = cartService.getCart(customer.getUser());
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setFulfillmentType(FulfillmentType.DELIVERY);
        order.setDeliveryAddress(deliveryAddress);
        order.setContactNumber(contactNumber);
        order.setTotalAmount(calculateTotal(cart));

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setMenu(cartItem.getMenu());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        return orderRepository.save(order);
    }

    public Order createOrderFromFrontend(Long restaurantId, String restaurantName, List<OrderItemRequest> items,
                                         String deliveryAddress, String deliveryCoordinates, String contactNumber,
                                         Double total, Customer customer) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Cannot create an order with an empty cart.");
        }

        for (OrderItemRequest item : items) {
            Menu menu = menuService.getMenuById(item.getMenuId());
            if (!menu.getRestaurant().getId().equals(restaurantId)) {
                throw new IllegalArgumentException("All items must be from the same restaurant.");
            }
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurantService.getRestaurantById(restaurantId));
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setFulfillmentType(FulfillmentType.DELIVERY);
        order.setDeliveryAddress(deliveryAddress);
        order.setDeliveryCoordinates(deliveryCoordinates);
        order.setContactNumber(contactNumber);
        order.setTotalAmount(total);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest item : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setMenu(menuService.getMenuById(item.getMenuId()));
            orderItem.setQuantity(item.getQuantity());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        return orderRepository.save(order);
    }

    private Double calculateTotal(Cart cart) {
        return cart.getItems().stream()
                .mapToDouble(item -> item.getMenu().getPrice() * item.getQuantity())
                .sum();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + id));
    }

    public List<Order> getOrdersWithoutPayments() {
        return orderRepository.findOrdersWithoutPayments();
    }

    public List<Order> getActiveOrdersByWaiter(Waiter waiter) {
        return orderRepository.findByWaiterAndStatus(waiter, "ACTIVE");
    }

    @Transactional
    public Order createOrder(Order order, Waiter waiter) {
        order.setWaiter(waiter);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("ACTIVE");
        order.setFulfillmentType(FulfillmentType.DINE_IN);
        order.setTotalAmount(order.getTotalAmount());
        return orderRepository.save(order);
    }

    @Transactional
    public void addItemToOrder(Long orderId, Long menuId, Integer quantity) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        Menu menu = menuService.getMenuById(menuId);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setMenu(menu);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(menu.getPrice() * quantity);

        order.getOrderItems().add(orderItem);
        order.setTotalAmount(calculateTotalAmount(order));
        orderRepository.save(order);
    }

    @Transactional
    public void removeItemFromOrder(Long orderId, Long orderItemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        OrderItem orderItem = order.getOrderItems().stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Order item not found with id: " + orderItemId));

        order.getOrderItems().remove(orderItem);
        order.setTotalAmount(calculateTotalAmount(order));
        orderRepository.save(order);
    }

    @Transactional
    public void completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        order.setStatus("COMPLETED");
        orderRepository.save(order);
    }

    public double calculateTotalAmount(Order order) {
        List<OrderItem> items = order.getOrderItems();
        if (items == null) {
            return 0.0;
        }
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}