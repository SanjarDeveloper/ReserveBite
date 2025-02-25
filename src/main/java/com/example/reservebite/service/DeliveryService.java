package com.example.reservebite.service;

import com.example.reservebite.entity.Delivery;
import com.example.reservebite.repository.DeliveryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    public List<Delivery> getAllDeliverys(){
        return deliveryRepository.findAll();
    }

    public void saveDelivery(Delivery delivery){
        deliveryRepository.save(delivery);
    }

    public Delivery getDeliveryByID(Long deliveryId){
        return deliveryRepository.findById(deliveryId).orElseThrow(() -> new NoSuchElementException("No such delivery"));
    }

    public void deleteDelivery(Long deliveryId){
        deliveryRepository.deleteById(deliveryId);
    }
}
