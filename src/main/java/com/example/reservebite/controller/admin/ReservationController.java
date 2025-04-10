package com.example.reservebite.controller.admin;

import com.example.reservebite.DTO.ReservationFormDTO;
import com.example.reservebite.DTO.SlotDTO;
import com.example.reservebite.entity.Reservation;
import com.example.reservebite.entity.Restaurant;
import com.example.reservebite.entity.Table;
import com.example.reservebite.repository.ReservationRepository;
import com.example.reservebite.service.*;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/reservations")
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;
    private final UsersService usersService;
    private final TableService tableService;
    private final RestaurantService restaurantService;

    public ReservationController(ReservationService reservationService, ReservationRepository reservationRepository, UsersService usersService, TableService tableService, RestaurantService restaurantService) {
        this.reservationService = reservationService;
        this.reservationRepository = reservationRepository;
        this.usersService = usersService;
        this.tableService = tableService;
        this.restaurantService = restaurantService;
    }

    // Custom binder for LocalDateTime to handle datetime-local input
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text != null && !text.isEmpty()) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                        setValue(LocalDateTime.parse(text, formatter));
                    } catch (DateTimeParseException e) {
                        setValue(null);
                    }
                } else {
                    setValue(null);
                }
            }

            @Override
            public String getAsText() {
                LocalDateTime value = (LocalDateTime) getValue();
                return value != null ? value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "";
            }
        });
    }

    @GetMapping("/all")
    public String showAllReservations(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sortBy", defaultValue = "reservationDate") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            Model model) {

        // Determine sort direction
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Create sort object
        Sort sort;
        switch (sortBy) {
            case "user.name":
                sort = Sort.by(direction, "user.name");
                break;
            case "restaurant.name":
                sort = Sort.by(direction, "restaurant.name");
                break;
            case "table.tableNumber":
                sort = Sort.by(direction, "table.tableNumber");
                break;
            default:
                sort = Sort.by(direction, sortBy);
                break;
        }

        // Fetch reservations with search and sort
        List<Reservation> reservations;
        if (search != null && !search.trim().isEmpty()) {
            reservations = reservationRepository.findBySearch(search);
            // Sort the filtered list
            reservations.sort((r1, r2) -> {
                int compare;
                switch (sortBy) {
                    case "numberOfGuests":
                        compare = r1.getNumberOfGuests().compareTo(r2.getNumberOfGuests());
                        break;
                    case "status":
                        compare = r1.getStatus().compareToIgnoreCase(r2.getStatus());
                        break;
                    case "user.name":
                        compare = r1.getUser().getName().compareToIgnoreCase(r2.getUser().getName());
                        break;
                    case "restaurant.name":
                        compare = r1.getRestaurant().getName().compareToIgnoreCase(r2.getRestaurant().getName());
                        break;
                    case "table.tableNumber":
                        // Handle null tables
                        String tableNumber1 = r1.getTable() != null ? String.valueOf(r1.getTable().getTableNumber()) : "";
                        String tableNumber2 = r2.getTable() != null ? String.valueOf(r2.getTable().getTableNumber()) : "";
                        compare = tableNumber1.compareToIgnoreCase(tableNumber2);
                        break;
                    case "reservationDate":
                    default:
                        compare = r1.getReservationDate().compareTo(r2.getReservationDate());
                        break;
                }
                return direction == Sort.Direction.ASC ? compare : -compare;
            });
        } else {
            reservations = reservationRepository.findAll(sort);
        }

        // Add attributes to the model
        model.addAttribute("reservations", reservations);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDir", sortDir);
        model.addAttribute("search", search);

        return "admin/reservations/list";
    }

    @GetMapping
    public String showReservationManagement(Model model) {
        // Total reservations
        model.addAttribute("totalReservations", reservationRepository.count());

        // Confirmed reservations
        model.addAttribute("confirmedReservations", reservationRepository.countByStatus("COMPLETED"));

        // Cancelled reservations
        model.addAttribute("cancelledReservations", reservationRepository.countByStatus("CANCELED"));

        // Today's reservations
        List<Reservation> todayReservations = reservationRepository.findByReservationDate(LocalDate.now());
        List<Reservation> todayReservationsWithNames = todayReservations.stream().map(reservation -> {
            reservation.setUser(reservation.getUser());
            reservation.setRestaurant(reservation.getRestaurant());
            return reservation;
        }).collect(Collectors.toList());
        model.addAttribute("todayReservations", todayReservationsWithNames);

        // Recent reservations (last 10)
        List<Reservation> recentReservations = reservationRepository.findTop10ByOrderByReservationDateDesc();
        List<Reservation> recentReservationsWithNames = recentReservations.stream().map(reservation -> {
            reservation.setUser(reservation.getUser());
            reservation.setRestaurant(reservation.getRestaurant());
            return reservation;
        }).collect(Collectors.toList());
        model.addAttribute("recentReservations", recentReservationsWithNames);

        return "admin/reservations/dashboard";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("reservationForm", new ReservationFormDTO());
        model.addAttribute("users", usersService.getUsersWithRoleUser());
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "admin/reservations/create";
    }

    @PostMapping
    public String createReservation(@ModelAttribute("reservationForm") ReservationFormDTO reservationForm, Model model) {
        // Validate required fields
        if (reservationForm.getReservationDate() == null) {
            model.addAttribute("error", "Reservation date is required.");
            model.addAttribute("users", usersService.getUsersWithRoleUser());
            model.addAttribute("restaurants", restaurantService.getAllRestaurants());
            return "admin/reservations/create";
        }
        if (reservationForm.getRestaurantId() == null) {
            model.addAttribute("error", "Please select a restaurant.");
            model.addAttribute("users", usersService.getUsersWithRoleUser());
            model.addAttribute("restaurants", restaurantService.getAllRestaurants());
            return "admin/reservations/create";
        }
        if (reservationForm.getUserId() == null) {
            model.addAttribute("error", "Please select a user.");
            model.addAttribute("users", usersService.getUsersWithRoleUser());
            model.addAttribute("restaurants", restaurantService.getAllRestaurants());
            return "admin/reservations/create";
        }
        if (reservationForm.getTableId() == null) {
            model.addAttribute("error", "Please select a table.");
            model.addAttribute("users", usersService.getUsersWithRoleUser());
            model.addAttribute("restaurants", restaurantService.getAllRestaurants());
            return "admin/reservations/create";
        }

        // Validate reservation date (not in the past)
        if (reservationForm.getReservationDate().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Reservation date cannot be in the past.");
            model.addAttribute("users", usersService.getUsersWithRoleUser());
            model.addAttribute("restaurants", restaurantService.getAllRestaurants());
            return "admin/reservations/create";
        }

        // Validate number of guests
        if (reservationForm.getNumberOfGuests() < 1) {
            model.addAttribute("error", "Number of guests must be at least 1.");
            model.addAttribute("users", usersService.getUsersWithRoleUser());
            model.addAttribute("restaurants", restaurantService.getAllRestaurants());
            return "admin/reservations/create";
        }

        // Check if the slot is already reserved
        List<Reservation> existingReservations = reservationRepository.findByTableIdAndDate(
                reservationForm.getTableId(),
                reservationForm.getReservationDate().toLocalDate()
        );
        LocalDateTime newStart = reservationForm.getReservationDate();
        LocalDateTime newEnd = newStart.plusHours(1);

        boolean isSlotTaken = existingReservations.stream()
                .filter(r -> !"CANCELED".equals(r.getStatus()))
                .anyMatch(r -> {
                    LocalDateTime resStart = r.getReservationDate();
                    LocalDateTime resEnd = resStart.plusHours(1);
                    return newStart.isBefore(resEnd) && newEnd.isAfter(resStart);
                });

        if (isSlotTaken) {
            model.addAttribute("error", "This table is already reserved for the selected time slot.");
            model.addAttribute("users", usersService.getUsersWithRoleUser());
            model.addAttribute("restaurants", restaurantService.getAllRestaurants());
            return "admin/reservations/create";
        }

        // Fetch managed entities and create the Reservation entity
        Reservation reservation = new Reservation();
        reservation.setReservationDate(reservationForm.getReservationDate());
        reservation.setNumberOfGuests(reservationForm.getNumberOfGuests());
        reservation.setStatus(reservationForm.getStatus());
        reservation.setRestaurant(restaurantService.getRestaurantById(reservationForm.getRestaurantId()));
        reservation.setUser(usersService.getUserById(reservationForm.getUserId()));
        reservation.setTable(tableService.getTableByID(reservationForm.getTableId()));

        reservationService.saveReservation(reservation);
        return "redirect:/admin/reservations";
    }
    // New endpoint for available slots
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
            LocalDateTime slotEnd = slotStart.plusHours(1);

            // Skip slots before the current time
            if (slotStart.isBefore(now)) {
                currentTime = currentTime.plusHours(1);
                continue;
            }

            // Check if the slot overlaps with any existing reservation
            boolean isReserved = reservations.stream()
                    .filter(r -> !"CANCELED".equals(r.getStatus()))
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
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.getReservationWithAssociations(id);

        // Map the Reservation entity to the DTO
        ReservationFormDTO reservationForm = new ReservationFormDTO();
        reservationForm.setId(reservation.getId());
        reservationForm.setReservationDate(reservation.getReservationDate());
        reservationForm.setNumberOfGuests(reservation.getNumberOfGuests());
        reservationForm.setStatus(reservation.getStatus());
        reservationForm.setRestaurantId(reservation.getRestaurant().getId());
        reservationForm.setUserId(reservation.getUser().getId());
        reservationForm.setTableId(reservation.getTable().getId());
        // Populate the display names
        reservationForm.setUserName(reservation.getUser().getName());
        reservationForm.setRestaurantName(reservation.getRestaurant().getName());

        model.addAttribute("reservationForm", reservationForm);
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        model.addAttribute("tables", tableService.getAvailableTablesByRestaurantId(reservation.getRestaurant().getId()));
        model.addAttribute("users", usersService.getUsersWithRoleUser());

        return "admin/reservations/edit";
    }

    @PostMapping("/update/{id}")
    public String updateReservation(@PathVariable Long id, @ModelAttribute("reservationForm") ReservationFormDTO reservationForm, Model model) {
        // Validate required fields
//        if (reservationForm.getReservationDate() == null) {
//            model.addAttribute("error", "Reservation date is required.");
//            model.addAttribute("restaurants", restaurantService.getAllRestaurants());
//            model.addAttribute("tables", tableService.getAvailableTablesByRestaurantId(reservationForm.getRestaurantId()));
//            model.addAttribute("users", usersService.getUsersWithRoleUser());
//            return "admin/reservations/edit";
//        }

        if (reservationForm.getTableId() == null) {
            model.addAttribute("error", "Please select a table.");
            model.addAttribute("restaurants", restaurantService.getAllRestaurants());
            model.addAttribute("tables", tableService.getAvailableTablesByRestaurantId(reservationForm.getRestaurantId()));
            model.addAttribute("users", usersService.getUsersWithRoleUser());
            return "admin/reservations/edit";
        }

        // Validate reservation date (not in the past)
//        if (reservationForm.getReservationDate().isBefore(LocalDateTime.now())) {
//            model.addAttribute("error", "Reservation date cannot be in the past.");
//            model.addAttribute("restaurants", restaurantService.getAllRestaurants());
//            model.addAttribute("tables", tableService.getAvailableTablesByRestaurantId(reservationForm.getRestaurantId()));
//            model.addAttribute("users", usersService.getUsersWithRoleUser());
//            return "admin/reservations/edit";
//        }

        // Validate number of guests
        if (reservationForm.getNumberOfGuests() < 1) {
            model.addAttribute("error", "Number of guests must be at least 1.");
            model.addAttribute("restaurants", restaurantService.getAllRestaurants());
            model.addAttribute("tables", tableService.getAvailableTablesByRestaurantId(reservationForm.getRestaurantId()));
            model.addAttribute("users", usersService.getUsersWithRoleUser());
            return "admin/reservations/edit";
        }

        // Update the existing Reservation entity
        Reservation reservation = reservationService.getReservationByID(id);
//        reservation.setReservationDate(reservationForm.getReservationDate()); // Update reservation date
        reservation.setNumberOfGuests(reservationForm.getNumberOfGuests());
        reservation.setStatus(reservationForm.getStatus());
        reservation.setTable(tableService.getTableByID(reservationForm.getTableId()));
        // Note: restaurant and user are not updated as they are non-editable

        reservationService.saveReservation(reservation);
        return "redirect:/admin/reservations";
    }

    @GetMapping("/cancel/{id}")
    public String cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return "redirect:/admin/reservations";
    }

    @GetMapping("/delete/{id}")
    public String deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return "redirect:/admin/reservations";
    }

    // Endpoint to fetch available tables for a restaurant and reservation date
    @GetMapping("/available-tables")
    @ResponseBody
    public ResponseEntity<List<Table>> getAvailableTables(
            @RequestParam("restaurantId") Long restaurantId,
            @RequestParam(value = "reservationDate", required = false) String reservationDate) {
        LocalDateTime dateTime = null;
        if (reservationDate != null && !reservationDate.isEmpty()) {
            try {
                dateTime = LocalDateTime.parse(reservationDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        List<Table> tables;
        if (dateTime != null) {
            tables = tableService.getAvailableTablesByRestaurantIdAndDate(restaurantId, dateTime);
        } else {
            tables = tableService.getTablesByRestaurantId(restaurantId); // Fetch all tables if no date
        }
        return ResponseEntity.ok(tables);
    }
}