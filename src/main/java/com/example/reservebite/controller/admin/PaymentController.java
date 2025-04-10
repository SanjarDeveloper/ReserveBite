package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.Payment;
import com.example.reservebite.entity.Order;
import com.example.reservebite.service.OrderService;
import com.example.reservebite.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/admin/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final OrderService orderService;

    public PaymentController(PaymentService paymentService, OrderService orderService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    @GetMapping
    public String listPayments(Model model) {
        model.addAttribute("payments", paymentService.getAllPayments());
        return "admin/payments/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        List<Order> orders = orderService.getOrdersWithoutPayments();
        if (orders.isEmpty()) {
            model.addAttribute("error", "No orders available without payments. Please create an order first or ensure existing orders do not have payments.");
            model.addAttribute("payments", paymentService.getAllPayments());
            return "admin/payments/list";
        }
        model.addAttribute("payment", new Payment());
        model.addAttribute("orders", orders);
        return "admin/payments/create";
    }

    @PostMapping
    public String createPayment(@ModelAttribute("payment") Payment payment, @RequestParam("orderId") Long orderId, Model model) {
        if (orderId == null) {
            model.addAttribute("error", "Please select an order.");
            model.addAttribute("orders", orderService.getOrdersWithoutPayments());
            return "admin/payments/create";
        }

        if (payment.getPaymentDateTime() == null) {
            payment.setPaymentDateTime(LocalDateTime.now());
        } else if (payment.getPaymentDateTime().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Payment date cannot be in the past.");
            model.addAttribute("orders", orderService.getOrdersWithoutPayments());
            return "admin/payments/create";
        }

        Order order = orderService.getOrderById(orderId);
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        paymentService.savePayment(payment);
        return "redirect:/admin/payments";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            Payment payment = paymentService.getPaymentByID(id);
            model.addAttribute("payment", payment);
            // We don't need to pass the orders list since the Order field is non-editable
            return "admin/payments/edit";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Payment not found.");
            model.addAttribute("payments", paymentService.getAllPayments());
            return "admin/payments/list";
        }
    }

    @PostMapping("/update/{id}")
    public String updatePayment(@PathVariable Long id, @ModelAttribute("payment") Payment payment, Model model) {
        Payment paymentByID;
        try {
            paymentByID = paymentService.getPaymentByID(id);
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Payment not found.");
            model.addAttribute("payments", paymentService.getAllPayments());
            return "admin/payments/list";
        }

        // Update fields, but do not change the Order
        paymentByID.setPaymentMethod(payment.getPaymentMethod());
        paymentByID.setPaymentStatus(payment.getPaymentStatus());
        paymentByID.setPaymentDateTime(payment.getPaymentDateTime());
        paymentByID.setAmount(payment.getAmount()); // This is read-only in the form, but included for completeness

        if (payment.getAmount() <= 0) {
            model.addAttribute("error", "Amount must be greater than 0.");
            model.addAttribute("payment", paymentByID);
            return "admin/payments/edit";
        }

        if (payment.getPaymentDateTime() == null || payment.getPaymentDateTime().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Payment date cannot be in the past.");
            model.addAttribute("payment", paymentByID);
            return "admin/payments/edit";
        }

        // The Order remains unchanged; no need to set it
        paymentService.savePayment(paymentByID);
        return "redirect:/admin/payments";
    }

    @GetMapping("/delete/{id}")
    public String deletePayment(@PathVariable Long id, Model model) {
        try {
            paymentService.deletePayment(id);
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Payment not found.");
        }
        return "redirect:/admin/payments";
    }
}