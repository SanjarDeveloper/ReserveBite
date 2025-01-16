package com.example.reservebite.service;

import com.example.reservebite.entity.Customer;
import com.example.reservebite.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;


    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }

    public void saveCustomer(Customer customer){
        customerRepository.save(customer);
    }

    public Customer getCustomerByID(Long customerId){
        return customerRepository.findById(customerId).orElseThrow(() -> new NoSuchElementException("No such customer"));
    }

    public void deleteCustomer(Long customerId){
        customerRepository.deleteById(customerId);
    }
}
