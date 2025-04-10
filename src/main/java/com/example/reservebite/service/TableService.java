package com.example.reservebite.service;

import com.example.reservebite.entity.Reservation;
import com.example.reservebite.entity.Table;
import com.example.reservebite.repository.ReservationRepository;
import com.example.reservebite.repository.TableRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TableService {
    private final TableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    public TableService(TableRepository tableRepository, ReservationRepository reservationRepository) {
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<Table> getAllTables() {
        return tableRepository.findAll();
    }

    public void saveTable(Table table) {
        tableRepository.save(table);
    }

    public Table getTableByID(Long tableId) {
        return tableRepository.findById(tableId).orElseThrow(() -> new NoSuchElementException("No such table"));
    }

    public void deleteTable(Long tableId) {
        tableRepository.deleteById(tableId);
    }

    public List<Table> getAvailableTablesByRestaurantId(Long restaurantId) {
        return tableRepository.findAvailableTablesByRestaurantId(restaurantId);
    }

    public List<Table> getAvailableTablesByRestaurantIdAndDate(Long restaurantId, LocalDateTime reservationDate) {
        // Fetch all tables for the restaurant
        List<Table> tables = tableRepository.findByRestaurantId(restaurantId);

        // Fetch reservations for the given date and restaurant
        List<Reservation> reservations = reservationRepository.findByRestaurantIdAndReservationDate(
                restaurantId, reservationDate);

        // Filter out tables that are already reserved at the given date and time
        List<Long> reservedTableIds = reservations.stream()
                .filter(reservation -> reservation.getTable() != null)
                .map(reservation -> reservation.getTable().getId())
                .collect(Collectors.toList());

        return tables.stream()
                .filter(table -> !reservedTableIds.contains(table.getId()))
                .collect(Collectors.toList());
    }
}
