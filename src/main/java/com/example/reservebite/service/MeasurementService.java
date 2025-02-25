package com.example.reservebite.service;

import com.example.reservebite.entity.Measurement;
import com.example.reservebite.repository.MeasurementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MeasurementService {
    private final MeasurementRepository measurementRepository;

    public MeasurementService(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    //get all measurements
    public List<Measurement> getAllMeasurements(){
        return measurementRepository.findAll();
    }

    //get one measurement by ID
    public Measurement getMeasurementById(Long id){
        return measurementRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No such Measurement with this ID"));
    }

    //Save measurement
    public void saveMeasurement(Measurement measurement){
        measurementRepository.save(measurement);
    }

    //Edit measurement
    public void editMeasurement(Long id, Measurement measurement){
        Measurement measurementById = getMeasurementById(id);
        measurementById.setName(measurement.getName());
        measurementById.setActive(measurement.isActive());
        saveMeasurement(measurementById);
    }

    //Delete measurement
    public void deleteMeasurement(Long id){
        measurementRepository.deleteById(id);
    }



}
