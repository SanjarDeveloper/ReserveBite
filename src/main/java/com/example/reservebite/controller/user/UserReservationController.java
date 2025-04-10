package com.example.reservebite.controller.user;

import com.example.reservebite.entity.Customer;
import com.example.reservebite.entity.Reservation;
import com.example.reservebite.entity.Table;
import com.example.reservebite.entity.Users;
import com.example.reservebite.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/user/reservations")
public class UserReservationController {
    private final ReservationService reservationService;
    private final CustomerService customerService;
    private final TableService tableService;
    private final RestaurantService restaurantService;

    private final UsersService usersService;

    public UserReservationController(ReservationService reservationService, CustomerService customerService, TableService tableService, RestaurantService restaurantService, UsersService usersService) {
        this.reservationService = reservationService;
        this.customerService = customerService;
        this.tableService = tableService;
        this.restaurantService = restaurantService;
        this.usersService = usersService;
    }

    @GetMapping
    public String listReservations(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        // Fetch the customer by username
        Users user = usersService.getUserByUsername(username);
        // Retrieve reservations for this customer
        List<Reservation> reservations = reservationService.getAllReservationsByUserId(user.getId());

        model.addAttribute("reservations", reservations);
        return "user/reservations/list";
    }


    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("tables", tableService.getAllTables());
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "user/reservations/reservation";
    }

    @PostMapping
    public String createReservation(@RequestParam("restaurantId") Long restaurantId,
                                    @RequestParam("tableId") Long tableId,
                                    @RequestParam("reservationDateTime")
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime reservationDateTime,
                                    @RequestParam("numberOfGuests") int numberOfGuests) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get username from authentication

        // Fetch your custom Users object from the database
        Users user = usersService.getUserByUsername(username);
        Reservation reservation = new Reservation();
        reservation.setRestaurant(restaurantService.getRestaurantById(restaurantId));
        reservation.setTable(tableService.getTableByID(tableId));
        reservation.setStatus("NEW");
        reservation.setUser(user);
        reservation.setNumberOfGuests(numberOfGuests);
        reservation.setReservationDate(reservationDateTime);
        reservationService.saveReservation(reservation);

        System.out.println(reservationDateTime);

        return "redirect:/user/reservations";
    }

    @GetMapping("/get-tables")
    @ResponseBody
    public List<Table> getTables(@RequestParam("restaurantId") Long restaurantId) {
        return tableService.getAvailableTablesByRestaurantId(restaurantId);
    }


    @GetMapping("/delete/{id}")
    public String deleteReservation(@PathVariable Long id) {
        Reservation reservationByID = reservationService.getReservationByID(id);
        reservationByID.setStatus("CANCELED");
        reservationService.saveReservation(reservationByID);
        return "redirect:/user/reservations";
    }
}
