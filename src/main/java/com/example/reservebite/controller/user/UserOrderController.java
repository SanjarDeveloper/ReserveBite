package com.example.reservebite.controller.user;

import com.example.reservebite.DTO.OrderRequest;
import com.example.reservebite.entity.Customer;
import com.example.reservebite.entity.Order;
import com.example.reservebite.entity.Users;
import com.example.reservebite.repository.UsersRepository;
import com.example.reservebite.service.CustomerService;
import com.example.reservebite.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserOrderController {
    private final OrderService orderService;
    private final UsersRepository usersRepository;
    private final CustomerService customerService;

    public UserOrderController(OrderService orderService, UsersRepository usersRepository, CustomerService customerService) {
        this.orderService = orderService;
        this.usersRepository = usersRepository;
        this.customerService = customerService;
    }

    @GetMapping("/my-orders")
    public String getMyOrders(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users user = usersRepository.findByUsername(username);
        Customer customer = customerService.getCustomerByUserID(user.getId());
        List<Order> orders = orderService.getOrdersByCustomer(customer);
        model.addAttribute("orders", orders);
        return "user/FoodDelivery/myOrders";
    }

    @PostMapping("/api/orders")
    @ResponseBody
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequest orderRequest) {
        try {
            if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Users user = usersRepository.findByUsername(username);
            Customer customer = customerService.getCustomerByUserID(user.getId());

            Order order = orderService.createOrderFromFrontend(
                    orderRequest.getRestaurantId(),
                    orderRequest.getRestaurantName(),
                    orderRequest.getItems(),
                    orderRequest.getDeliveryAddress(),
                    orderRequest.getDeliveryCoordinates(),
                    orderRequest.getContactNumber(),
                    orderRequest.getTotal(),
                    customer
            );
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Or include e.getMessage() in a custom response
        }
    }
}
