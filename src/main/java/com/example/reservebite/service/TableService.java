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
        return tableRepository.findById(tableId)
                .orElseThrow(() -> new NoSuchElementException("Table with ID " + tableId + " not found"));
    }

    public void deleteTable(Long tableId) {
        tableRepository.deleteById(tableId);
    }

    public List<Table> getAvailableTablesByRestaurantId(Long restaurantId) {
        return tableRepository.findAvailableTablesByRestaurantId(restaurantId);
    }

    public List<Table> getTablesByRestaurantId(Long restaurantId) {
        if (restaurantId == null) {
            throw new IllegalArgumentException("Restaurant ID must not be null");
        }
        return tableRepository.findByRestaurantId(restaurantId);
    }

    public List<Table> getAvailableTablesByRestaurantIdAndDate(Long restaurantId, LocalDateTime dateTime) {
        if (restaurantId == null || dateTime == null) {
            throw new IllegalArgumentException("Restaurant ID and dateTime must not be null");
        }
        LocalDateTime endTime = dateTime.plusHours(1);
        return tableRepository.findAvailableTablesByRestaurantIdAndDateRange(restaurantId, dateTime, endTime);
    }

}
