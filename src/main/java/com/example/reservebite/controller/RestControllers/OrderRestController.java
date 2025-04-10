package com.example.reservebite.controller.RestControllers;

import com.example.reservebite.DTO.OrderItemRequest;
import com.example.reservebite.DTO.OrderRequest;
import com.example.reservebite.entity.*;
import com.example.reservebite.entity.enums.FulfillmentType;
import com.example.reservebite.repository.OrderItemRepository;
import com.example.reservebite.repository.RestaurantRepository;
import com.example.reservebite.repository.UsersRepository;
import com.example.reservebite.service.CustomerService;
import com.example.reservebite.service.DeliveryPersonService;
import com.example.reservebite.service.DeliveryService;
import com.example.reservebite.service.MenuService;
import com.example.reservebite.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {
    private final OrderService orderService;
    private final CustomerService customerService;
    private final MenuService menuService;
    private final DeliveryService deliveryService; // Add DeliveryService
    private final DeliveryPersonService deliveryPersonService; // Add DeliveryPersonService
    private final UsersRepository usersRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderRestController(OrderService orderService, CustomerService customerService, MenuService menuService,
                               DeliveryService deliveryService, DeliveryPersonService deliveryPersonService,
                               UsersRepository usersRepository, RestaurantRepository restaurantRepository,
                               OrderItemRepository orderItemRepository) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.menuService = menuService;
        this.deliveryService = deliveryService;
        this.deliveryPersonService = deliveryPersonService;
        this.usersRepository = usersRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping
    @Transactional
    public Order placeOrder(@RequestBody OrderRequest orderRequest) {
        System.out.println("Received OrderRequest: " + orderRequest);

        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users user = usersRepository.findByUsername(username);

        // Check if the user is a customer; create one if not
        boolean customerExist = customerService.isCustomerExist(user.getId());
        Customer customer;
        if (!customerExist) {
            customer = customerService.createCustomerFromOrderRequest(orderRequest, user);
        } else {
            customer = customerService.getCustomerByUserID(user.getId());
        }

        // Fetch the existing Restaurant from the database
        Restaurant restaurant = restaurantRepository.findById(orderRequest.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // Create the Order
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setFulfillmentType(FulfillmentType.DELIVERY);
        order.setTotalAmount(orderRequest.getTotal());
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setDeliveryAddress(orderRequest.getDeliveryAddress());
        order.setDeliveryCoordinates(orderRequest.getDeliveryCoordinates());
        order.setContactNumber(orderRequest.getContactNumber());

        // Create OrderItems
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setMenu(menuService.getMenuById(itemRequest.getMenuId()));
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        // Save the Order
        Order savedOrder = orderService.saveOrderAndReturn(order);

        // Associate the Order with the OrderItems and save them
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(savedOrder);
        }
        orderItemRepository.saveAll(orderItems);

        // Create a Delivery record if fulfillmentType is DELIVERY
        if (savedOrder.getFulfillmentType() == FulfillmentType.DELIVERY) {
            Delivery delivery = new Delivery();
            delivery.setOrder(savedOrder);
            delivery.setDeliveryAddress(savedOrder.getDeliveryAddress());
            delivery.setDeliveryTime(LocalDateTime.now().plusHours(1)); // Example: Set delivery time to 1 hour from now
            delivery.setDeliveryFee("5.00"); // Example: Set a default delivery fee

            // Assign a courier
            DeliveryPerson courier = deliveryPersonService.selectAvailableCourier();
            delivery.setCourier(courier);

            // Save the Delivery record
            deliveryService.saveDelivery(delivery);
        }

        return savedOrder;
    }
}