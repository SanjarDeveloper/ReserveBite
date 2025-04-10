package com.example.reservebite.controller.waiter;

import com.example.reservebite.DTO.CreateOrderRequest;
import com.example.reservebite.DTO.OrderItemRequest;
import com.example.reservebite.entity.*;
import com.example.reservebite.service.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Controller
@RequestMapping("/waiter")
public class WaiterController {
    private static final Logger logger = LoggerFactory.getLogger(WaiterController.class);

    private final OrderService orderService;
    private final TableService tableService;
    private final MenuService menuService;
    private final WaiterService waiterService;
    private final DineInService dineInService;
    private final UsersService usersService;

    public WaiterController(OrderService orderService, TableService tableService, MenuService menuService,
                            WaiterService waiterService, DineInService dineInService, UsersService usersService) {
        this.orderService = orderService;
        this.tableService = tableService;
        this.menuService = menuService;
        this.waiterService = waiterService;
        this.dineInService = dineInService;
        this.usersService = usersService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Authentication authentication) {
        Users user = usersService.getUserByUsername(authentication.getName());
        Waiter waiter = waiterService.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Waiter not found"));

        model.addAttribute("orders", orderService.getActiveOrdersByWaiter(waiter));
        model.addAttribute("tables", tableService.getAvailableTablesByRestaurantId(waiter.getRestaurant().getId()));
        return "waiter/dashboard";
    }

    @GetMapping("/orders/active")
    @ResponseBody
    public List<Order> getActiveOrders(Authentication authentication) {
        Users user = usersService.getUserByUsername(authentication.getName());
        Waiter waiter = waiterService.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Unauthorized access"));
        return orderService.getActiveOrdersByWaiter(waiter);
    }

    @Transactional
    @PostMapping("/orders")
    @ResponseBody
    public ResponseEntity<Order> createOrder(@RequestBody @Valid CreateOrderRequest request, Authentication authentication) {
        logger.info("Creating new order for table ID: {}", request.getTableId());
        Users user = usersService.getUserByUsername(authentication.getName());
        Waiter waiter = waiterService.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Unauthorized access"));

        Table table = tableService.getTableByID(request.getTableId());
        if (table == null) {
            throw new ResponseStatusException(NOT_FOUND, "Table not found");
        }
        if (!table.getRestaurant().getId().equals(waiter.getRestaurant().getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid table selection");
        }

        Order order = new Order();
        order.setRestaurant(waiter.getRestaurant());
        order.setTable(table);
        order.setOrderItems(new ArrayList<>());
        order.setTotalAmount(Double.valueOf(request.getTotal()));
        Order savedOrder = orderService.createOrder(order, waiter);
        orderService.saveOrder(savedOrder);

        DineIn dineIn = new DineIn();
        dineIn.setOrder(savedOrder);
        dineIn.setTable(table);
        dineIn.setWaiter(waiter.getUser());
        dineIn.setStatus("ACTIVE");
        dineInService.saveDineIn(dineIn);

        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping("/orders/{id}")
    @ResponseBody
    public Order getOrder(@PathVariable Long id, Authentication authentication) {
        Users user = usersService.getUserByUsername(authentication.getName());
        Waiter waiter = waiterService.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Unauthorized access"));

        Order order = orderService.getOrderById(id);
        if (!order.getRestaurant().getId().equals(waiter.getRestaurant().getId())) {
            throw new ResponseStatusException(FORBIDDEN, "Access to order denied");
        }
        return order;
    }

    @PostMapping("/orders/{id}/add-item")
    @ResponseBody
    public ResponseEntity<Order> addItemToOrder(@PathVariable Long id, @RequestBody @Valid OrderItemRequest request,
                                                Authentication authentication) {
        verifyOrderAccess(id, authentication);
        orderService.addItemToOrder(id, request.getMenuId(), request.getQuantity());
        Order updatedOrder = orderService.getOrderById(id);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/orders/{id}/remove-item")
    @ResponseBody
    public ResponseEntity<Order> removeItemFromOrder(@PathVariable Long id, @RequestBody @Valid OrderItemRequest request,
                                                     Authentication authentication) {
        if (request.getOrderItemId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Order item ID is required");
        }
        verifyOrderAccess(id, authentication);
        orderService.removeItemFromOrder(id, request.getOrderItemId());
        Order updatedOrder = orderService.getOrderById(id);
        return ResponseEntity.ok(updatedOrder);
    }

    @Transactional
    @PostMapping("/orders/{id}/complete")
    @ResponseBody
    public ResponseEntity<Void> completeOrder(@PathVariable Long id, Authentication authentication) {
        verifyOrderAccess(id, authentication);
        orderService.completeOrder(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/menu-items")
    @ResponseBody
    public List<Menu> getMenuItems(Authentication authentication) {
        Users user = usersService.getUserByUsername(authentication.getName());
        Waiter waiter = waiterService.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Waiter not found"));
        return menuService.getMenusByRestaurantId(waiter.getRestaurant().getId());
    }

    private void verifyOrderAccess(Long orderId, Authentication authentication) {
        Users user = usersService.getUserByUsername(authentication.getName());
        Waiter waiter = waiterService.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Unauthorized access"));

        Order order = orderService.getOrderById(orderId);
        if (!order.getRestaurant().getId().equals(waiter.getRestaurant().getId())) {
            throw new ResponseStatusException(FORBIDDEN, "Access to order denied");
        }
    }
}