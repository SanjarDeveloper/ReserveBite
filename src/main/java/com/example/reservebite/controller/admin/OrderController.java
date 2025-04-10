package com.example.reservebite.controller.admin;

import com.example.reservebite.DTO.OrderDTO;
import com.example.reservebite.entity.*;
import com.example.reservebite.repository.*;
import com.example.reservebite.service.CustomerService;
import com.example.reservebite.service.OrderService;
import com.example.reservebite.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final RestaurantService restaurantService;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryPersonRepository deliveryPersonRepository;
    private final PaymentRepository paymentRepository;
    private final DineInRepository dineInRepository;

    public OrderController(OrderService orderService, OrderRepository orderRepository, CustomerService customerService, RestaurantService restaurantService, DeliveryRepository deliveryRepository, DeliveryPersonRepository deliveryPersonRepository, PaymentRepository paymentRepository, DineInRepository dineInRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.restaurantService = restaurantService;
        this.deliveryRepository = deliveryRepository;
        this.deliveryPersonRepository = deliveryPersonRepository;
        this.paymentRepository = paymentRepository;
        this.dineInRepository = dineInRepository;
    }

    @GetMapping
    public String dashboardOrders(Model model) {
        // Total orders
        model.addAttribute("totalOrders", orderRepository.count());

        // Pending orders
        model.addAttribute("pendingOrders", orderRepository.countPendingOrders());

        // Completed orders
        model.addAttribute("completedOrders", orderRepository.countCompletedOrders());

        // Total revenue
        Double totalRevenue = orderRepository.calculateTotalRevenue();
        model.addAttribute("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);

        // Today's orders
        List<Order> todayOrders = orderRepository.findOrdersMadeToday();
        model.addAttribute("todayOrders", todayOrders);

        // Recent orders (last 10)
        List<Order> recentOrders = orderRepository.findTop10ByOrderByOrderDateDesc();
        model.addAttribute("recentOrders", recentOrders);

        // Recent deliveries
        List<Delivery> recentDeliveries = deliveryRepository.findTop5ByOrderByDeliveryTimeDesc();
        model.addAttribute("recentDeliveries", recentDeliveries != null ? recentDeliveries : Collections.emptyList());

        // Couriers
        List<DeliveryPerson> couriers = deliveryPersonRepository.findTop5ByOrderByIdDesc();
        model.addAttribute("couriers", couriers != null ? couriers : Collections.emptyList());

        // Recent payments
        List<Payment> recentPayments = paymentRepository.findTop5ByOrderByIdDesc();
        model.addAttribute("recentPayments", recentPayments != null ? recentPayments : Collections.emptyList());

        // Recent dine-ins
        List<DineIn> recentDineIns = dineInRepository.findTop5ByOrderByIdDesc();
        model.addAttribute("recentDineIns", recentDineIns != null ? recentDineIns : Collections.emptyList());
        return "admin/orders/dashboard";
    }

    @GetMapping("/all")
    public String showAllOrders(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sortBy", defaultValue = "orderDate") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            Model model) {

        // Determine sort direction
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Create sort object
        Sort sort;
        switch (sortBy) {
            case "customerName":
                sort = Sort.by(direction, "customer.user.name");
                break;
            case "restaurantName":
                sort = Sort.by(direction, "restaurant.name");
                break;
            default:
                sort = Sort.by(direction, sortBy);
                break;
        }

        // Fetch orders with search and sort
        List<Order> orders;
        if (search != null && !search.trim().isEmpty()) {
            orders = orderRepository.findBySearch(search);
            // Sort the filtered list
            orders.sort((o1, o2) -> {
                int compare;
                switch (sortBy) {
                    case "totalAmount":
                        compare = o1.getTotalAmount().compareTo(o2.getTotalAmount());
                        break;
                    case "status":
                        compare = o1.getStatus().compareToIgnoreCase(o2.getStatus());
                        break;
                    case "fulfillmentType":
                        compare = o1.getFulfillmentType().compareTo(o2.getFulfillmentType());
                        break;
                    case "customerName":
                        compare = o1.getCustomer().getUser().getName().compareToIgnoreCase(o2.getCustomer().getUser().getName());
                        break;
                    case "restaurantName":
                        compare = o1.getRestaurant().getName().compareToIgnoreCase(o2.getRestaurant().getName());
                        break;
                    case "orderDate":
                    default:
                        compare = o1.getOrderDate().compareTo(o2.getOrderDate());
                        break;
                }
                return direction == Sort.Direction.ASC ? compare : -compare;
            });
        } else {
            orders = orderRepository.findAll(sort);
        }

        // Convert Order entities to OrderDTO for the view
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        List<OrderDTO> orderDTOs = orders.stream()
                .map(order -> new OrderDTO(
                        order.getId(),
                        order.getOrderDate().format(formatter),
                        order.getTotalAmount(),
                        order.getStatus(),
                        order.getFulfillmentType(),
                        Optional.ofNullable(order.getCustomer())
                                .map(Customer::getUser)
                                .map(Users::getName)
                                .orElse(""),
                        order.getRestaurant().getName()
                ))
                .collect(Collectors.toList());

        // Add attributes to the model
        model.addAttribute("orders", orderDTOs);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDir", sortDir);
        model.addAttribute("search", search);

        return "admin/orders/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("order", new Order());
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        model.addAttribute("customers", customerService.getAllCustomers());
        return "admin/orders/create";
    }

    @PostMapping
    public String createOrder(@Valid @ModelAttribute("order") Order order, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("restaurants", restaurantService.getAllRestaurants());
            model.addAttribute("customers", customerService.getAllCustomers());
            return "admin/orders/create";
        }
        orderService.saveOrder(order);
        return "redirect:/admin/orders";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderByID(id);
        model.addAttribute("order", order);
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        model.addAttribute("customers", customerService.getAllCustomers());
        return "admin/orders/edit";
    }

    @PostMapping("/update/{id}")
    public String updateOrder(@PathVariable Long id, @ModelAttribute("order") Order order) {
        Order orderByID = orderService.getOrderByID(id);
        orderByID.setOrderDate(order.getOrderDate());
        orderByID.setOrderItems(order.getOrderItems());
        orderByID.setFulfillmentType(order.getFulfillmentType());
        orderByID.setRestaurant(order.getRestaurant());
        orderByID.setTotalAmount(order.getTotalAmount());
        orderByID.setStatus(order.getStatus());
        orderByID.setCustomer(order.getCustomer());
        orderService.saveOrder(orderByID);
        return "redirect:/admin/orders";
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return "redirect:/admin/orders";
    }
}