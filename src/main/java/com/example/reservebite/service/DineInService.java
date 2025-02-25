package com.example.reservebite.service;

import com.example.reservebite.entity.DineIn;
import com.example.reservebite.repository.DineInRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DineInService {
    private final DineInRepository dineInRepository;


    public DineInService(DineInRepository dineInRepository) {
        this.dineInRepository = dineInRepository;
    }

    public List<DineIn> getAllDineIns(){
        return dineInRepository.findAll();
    }

    public void saveDineIn(DineIn dineIn){
        dineInRepository.save(dineIn);
    }

    public DineIn getDineInByID(Long dineInId){
        return dineInRepository.findById(dineInId).orElseThrow(() -> new NoSuchElementException("No such dineIn"));
    }

    public void deleteDineIn(Long dineInId){
        dineInRepository.deleteById(dineInId);
    }
}
