package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.Payment;
import com.example.reservebite.service.OrderService;
import com.example.reservebite.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        model.addAttribute("payment", new Payment());
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/payments/create";
    }

    @PostMapping
    public String createPayment(@ModelAttribute("payment") Payment payment) {
        paymentService.savePayment(payment);
        return "redirect:/admin/payments";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Payment payment = paymentService.getPaymentByID(id);
        model.addAttribute("payment", payment);
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/payments/edit";
    }

    @PostMapping("/update/{id}")
    public String updatePayment(@PathVariable Long id, @ModelAttribute("payment") Payment payment) {
        Payment paymentByID = paymentService.getPaymentByID(id);
        paymentByID.setAmount(payment.getAmount());
        paymentByID.setPaymentMethod(payment.getPaymentMethod());
        paymentByID.setPaymentStatus(payment.getPaymentStatus());
        paymentByID.setPaymentDateTime(payment.getPaymentDateTime());
        paymentByID.setOrder(payment.getOrder());
        paymentService.savePayment(paymentByID);
        return "redirect:/admin/payments";
    }


    @GetMapping("/delete/{id}")
    public String deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return "redirect:/admin/payments";
    }
}
