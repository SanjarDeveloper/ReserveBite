package com.example.reservebite.service;

import com.example.reservebite.entity.Reservation;
import com.example.reservebite.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;


    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> getAllReservations(){
        return reservationRepository.findAll();
    }

    public void saveReservation(Reservation reservation){
        reservationRepository.save(reservation);
    }

    public Reservation getReservationByID(Long reservationId){
        return reservationRepository.findById(reservationId).orElseThrow(() -> new NoSuchElementException("No such reservation"));
    }

    public void deleteReservation(Long reservationId){
        reservationRepository.deleteById(reservationId);
    }
}
