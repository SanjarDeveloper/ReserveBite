package com.example.reservebite.controller.user;

import com.example.reservebite.DTO.ReservationFormDTO;
import com.example.reservebite.DTO.SlotDTO;
import com.example.reservebite.entity.Reservation;
import com.example.reservebite.entity.Restaurant;
import com.example.reservebite.entity.Table;
import com.example.reservebite.repository.ReservationRepository;
import com.example.reservebite.service.ReservationService;
import com.example.reservebite.service.RestaurantService;
import com.example.reservebite.service.TableService;
import com.example.reservebite.service.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/reservations")
public class UserReservationController {
    private final ReservationService reservationService;
    private final RestaurantService restaurantService;
    private final TableService tableService;
    private final UsersService usersService;
    private final ReservationRepository reservationRepository;

    public UserReservationController(ReservationService reservationService, RestaurantService restaurantService, TableService tableService, UsersService usersService, ReservationRepository reservationRepository) {
        this.reservationService = reservationService;
        this.restaurantService = restaurantService;
        this.tableService = tableService;
        this.usersService = usersService;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping
    public String showReservationPage(Model model) {
        model.addAttribute("reservations", reservationService.getAllReservations());
        return "user/reservations/reservation";
    }

    @GetMapping("/restaurants")
    @ResponseBody
    public List<Restaurant> getRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @GetMapping("/my-reservations")
    @ResponseBody
    public List<ReservationFormDTO> getMyReservations(Authentication authentication) {
        String username = authentication.getName();
        List<Reservation> reservations = reservationRepository.findByUserUsername(username);
        return reservations.stream().map(reservation -> {
            ReservationFormDTO dto = new ReservationFormDTO();
            dto.setId(reservation.getId());
            dto.setReservationDate(reservation.getReservationDate());
            dto.setNumberOfGuests(reservation.getNumberOfGuests());
            dto.setStatus(reservation.getStatus());
            dto.setRestaurantId(reservation.getRestaurant() != null ? reservation.getRestaurant().getId() : null);
            dto.setRestaurantName(reservation.getRestaurant() != null ? reservation.getRestaurant().getName() : "N/A");
            dto.setTableId(reservation.getTable() != null ? reservation.getTable().getId() : null);
            dto.setTableNumber(reservation.getTable() != null ? String.valueOf(reservation.getTable().getTableNumber()) : "N/A");
            dto.setUserId(reservation.getUser() != null ? reservation.getUser().getId() : null);
            dto.setUserName(reservation.getUser() != null ? reservation.getUser().getUsername() : "N/A");
            return dto;
        }).collect(Collectors.toList());
    }

    @GetMapping("/tables")
    @ResponseBody
    public List<Table> getTables(@RequestParam("restaurantId") Long restaurantId) {
        return tableService.getTablesByRestaurantId(restaurantId);
    }

    @GetMapping("/available-slots")
    @ResponseBody
    public ResponseEntity<List<SlotDTO>> getAvailableSlots(
            @RequestParam("restaurantId") Long restaurantId,
            @RequestParam("tableId") Long tableId,
            @RequestParam("date") String date) {
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }

        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        if (restaurant == null) {
            return ResponseEntity.badRequest().build();
        }

        LocalTime openTime = restaurant.getOpenTime();
        LocalTime closeTime = restaurant.getCloseTime();
        List<Reservation> reservations = reservationRepository.findByTableIdAndDate(tableId, localDate);
        LocalDateTime now = LocalDateTime.now();

        List<SlotDTO> slots = new ArrayList<>();
        LocalTime currentTime = openTime;

        while (currentTime.isBefore(closeTime)) {
            LocalDateTime slotStart = localDate.atTime(currentTime);
            if (slotStart.isBefore(now)) {
                currentTime = currentTime.plusHours(1);
                continue;
            }

            LocalDateTime slotEnd = slotStart.plusHours(1);
            boolean isReserved = reservations.stream()
                    .filter(r -> !"CANCELLED".equals(r.getStatus()))
                    .anyMatch(r -> {
                        LocalDateTime resStart = r.getReservationDate();
                        LocalDateTime resEnd = resStart.plusHours(1);
                        return slotStart.isBefore(resEnd) && slotEnd.isAfter(resStart);
                    });

            slots.add(new SlotDTO(currentTime.format(DateTimeFormatter.ofPattern("HH:mm")), !isReserved));
            currentTime = currentTime.plusHours(1);
        }

        return ResponseEntity.ok(slots);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<String> createReservation(@RequestBody ReservationFormDTO reservationDTO, Authentication authentication) {
        String username = authentication.getName();

        // Validate the DTO
        if (reservationDTO.getRestaurantId() == null || reservationDTO.getTableId() == null) {
            return ResponseEntity.badRequest().body("Restaurant ID and Table ID are required.");
        }
        if (reservationDTO.getReservationDate() == null) {
            return ResponseEntity.badRequest().body("Reservation date is required.");
        }

        // Create a new Reservation entity
        Reservation reservation = new Reservation();
        reservation.setUser(usersService.getUserByUsername(username));

        // Fetch Restaurant and Table entities
        Restaurant restaurant = restaurantService.getRestaurantById(reservationDTO.getRestaurantId());
        if (restaurant == null) {
            return ResponseEntity.badRequest().body("Invalid restaurant ID.");
        }
        reservation.setRestaurant(restaurant);

        Table table = tableService.getTableByID(reservationDTO.getTableId());
        if (table == null) {
            return ResponseEntity.badRequest().body("Invalid table ID.");
        }
        reservation.setTable(table);

        // Set other fields
        reservation.setReservationDate(reservationDTO.getReservationDate()); // No need to parse, already a LocalDateTime
        reservation.setNumberOfGuests(reservationDTO.getNumberOfGuests());
        reservation.setStatus(reservationDTO.getStatus() != null ? reservationDTO.getStatus() : "ACTIVE");

        // Validate number of guests
        if (reservationDTO.getNumberOfGuests() <= 0) {
            return ResponseEntity.badRequest().body("Number of guests must be at least 1.");
        }

        // Validate reservation time
        LocalDateTime startTime = reservation.getReservationDate();
        LocalDateTime endTime = startTime.plusHours(1);

        if (startTime.isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Reservation time cannot be in the past.");
        }

        // Check for existing reservations
        List<Reservation> existingReservations = reservationRepository.findByTableIdAndDate(
                reservation.getTable().getId(), startTime.toLocalDate());
        boolean isReserved = existingReservations.stream()
                .filter(r -> !"CANCELLED".equals(r.getStatus()))
                .anyMatch(r -> {
                    LocalDateTime resStart = r.getReservationDate();
                    LocalDateTime resEnd = resStart.plusHours(1);
                    return startTime.isBefore(resEnd) && endTime.isAfter(resStart);
                });

        if (isReserved) {
            return ResponseEntity.badRequest().body("This table is already reserved for the selected time.");
        }

        // Save the reservation
        reservationService.saveReservation(reservation);
        return ResponseEntity.ok("Reservation created successfully.");
    }

    @GetMapping("/cancel/{id}")
    public String cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return "redirect:/user/reservations";
    }
}