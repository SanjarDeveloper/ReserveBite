package com.example.reservebite.service;

import com.example.reservebite.entity.Reservation;
import com.example.reservebite.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public Reservation getReservationWithAssociations(Long id) {
        return reservationRepository.findByIdWithAssociations(id);
    }

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

    public List<Reservation> getAllReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }
    public void cancelReservation(Long reservationId) {
        Reservation reservation = getReservationByID(reservationId);
        reservation.setStatus("CANCELED");
        reservationRepository.save(reservation);
    }

    public void deleteReservation(Long reservationId){
        reservationRepository.deleteById(reservationId);
    }
}
