package com.example.reservebite.service;

import com.example.reservebite.entity.DeliveryPerson;
import com.example.reservebite.repository.DeliveryPersonRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
public class DeliveryPersonService {

    private final DeliveryPersonRepository deliveryPersonRepository;

    public DeliveryPersonService(DeliveryPersonRepository deliveryPersonRepository) {
        this.deliveryPersonRepository = deliveryPersonRepository;
    }

    public List<DeliveryPerson> getAllDeliveryPersons() {
        return deliveryPersonRepository.findAll();
    }

    public DeliveryPerson getDeliveryPersonById(Long id) {
        return deliveryPersonRepository.findById(id).orElse(null);
    }

    public DeliveryPerson saveDeliveryPerson(DeliveryPerson deliveryPerson) {
        return deliveryPersonRepository.save(deliveryPerson);
    }

    public void deleteDeliveryPerson(Long id) {
        deliveryPersonRepository.deleteById(id);
    }

    // New method to select an available courier
    public DeliveryPerson selectAvailableCourier() {
        List<DeliveryPerson> activeCouriers = deliveryPersonRepository.findByIsActiveTrue();
        if (activeCouriers.isEmpty()) {
            throw new RuntimeException("No active couriers available");
        }
        // Select a random active courier
        Random random = new Random();
        return activeCouriers.get(random.nextInt(activeCouriers.size()));
    }
}
