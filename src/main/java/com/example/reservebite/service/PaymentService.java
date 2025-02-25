package com.example.reservebite.service;

import com.example.reservebite.entity.Payment;
import com.example.reservebite.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;


    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> getAllPayments(){
        return paymentRepository.findAll();
    }

    public void savePayment(Payment payment){
        paymentRepository.save(payment);
    }

    public Payment getPaymentByID(Long paymentId){
        return paymentRepository.findById(paymentId).orElseThrow(() -> new NoSuchElementException("No such payment"));
    }

    public void deletePayment(Long paymentId){
        paymentRepository.deleteById(paymentId);
    }
}
