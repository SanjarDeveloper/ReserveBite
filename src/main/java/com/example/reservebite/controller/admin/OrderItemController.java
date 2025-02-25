package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.OrderItem;
import com.example.reservebite.service.MenuService;
import com.example.reservebite.service.OrderItemService;
import com.example.reservebite.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/order-items")
public class OrderItemController {
    private final OrderItemService orderItemService;
    private final MenuService menuService;
    private final OrderService orderService;

    public OrderItemController(OrderItemService orderItemService, MenuService menuService, OrderService orderService) {
        this.orderItemService = orderItemService;
        this.menuService = menuService;
        this.orderService = orderService;
    }

    @GetMapping
    public String listOrderItems(Model model) {
        model.addAttribute("orderItems", orderItemService.getAllOrderItems());
        return "admin/orderitems/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("orderItem", new OrderItem());
        model.addAttribute("menus", menuService.getAllMenus());
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/orderitems/create";
    }

    @PostMapping
    public String createOrderItem(@ModelAttribute("orderItem") OrderItem orderItem) {
        orderItemService.saveOrderItem(orderItem);
        return "redirect:/admin/order-items";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        OrderItem orderItem = orderItemService.getOrderItemByID(id);
        model.addAttribute("menus", menuService.getAllMenus());
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("orderItem", orderItem);
        return "admin/orderitems/edit";
    }

    @PostMapping("/update/{id}")
    public String updateOrderItem(@PathVariable Long id, @ModelAttribute("orderItem") OrderItem orderItem) {
        OrderItem orderItemByID = orderItemService.getOrderItemByID(id);
        orderItemByID.setMenu(orderItem.getMenu());
        orderItemByID.setOrder(orderItem.getOrder());
        orderItemByID.setQuantity(orderItem.getQuantity());
        orderItemService.saveOrderItem(orderItemByID);
        return "redirect:/admin/order-items";
    }


    @GetMapping("/delete/{id}")
    public String deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return "redirect:/admin/order-items";
    }
}
