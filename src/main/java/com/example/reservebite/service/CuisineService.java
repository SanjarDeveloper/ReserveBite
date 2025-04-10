package com.example.reservebite.service;

import com.example.reservebite.entity.Cuisine;
import com.example.reservebite.repository.CuisineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CuisineService {
    private final CuisineRepository cuisineRepository;

    public CuisineService(CuisineRepository cuisineRepository) {
        this.cuisineRepository = cuisineRepository;
    }

    //get all cuisines
    public List<Cuisine> getAllCuisines(){
        return cuisineRepository.findAll();
    }

    public List<Cuisine> getAllActiveCuisines(){
        return cuisineRepository.findAllByIsActiveTrue();
    }

    //get one cuisine by ID
    public Cuisine getCuisineById(Long id){
        return cuisineRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No such Cuisine with this ID"));
    }

    //Save cuisine
    public void saveCuisine(Cuisine cuisine){
        cuisineRepository.save(cuisine);
    }

    //Edit cuisine
    public void editCuisine(Long id, Cuisine cuisine){
        Cuisine cuisineById = getCuisineById(id);
        cuisineById.setName(cuisine.getName());
        cuisineById.setActive(cuisine.isActive());
        saveCuisine(cuisineById);
    }

    //Delete cuisine
    public void deleteCuisine(Long id){
        cuisineRepository.deleteById(id);

    }


}
