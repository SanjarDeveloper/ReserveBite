package com.example.reservebite.controller.user;

import com.example.reservebite.entity.Reservation;
import com.example.reservebite.service.CustomerService;
import com.example.reservebite.service.ReservationService;
import com.example.reservebite.service.RestaurantService;
import com.example.reservebite.service.TableService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/user/reservations")
public class UserReservationController {
    private final ReservationService reservationService;
    private final CustomerService customerService;
    private final TableService tableService;
    private final RestaurantService restaurantService;

    public UserReservationController(ReservationService reservationService, CustomerService customerService, TableService tableService, RestaurantService restaurantService) {
        this.reservationService = reservationService;
        this.customerService = customerService;
        this.tableService = tableService;
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public String listReservations(Model model) {
        model.addAttribute("reservations", reservationService.getAllReservations());
        return "admin/reservations/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("tables", tableService.getAllTables());
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "user/Reservation/ReservationForm";
    }

    @PostMapping
    public String createReservation(@ModelAttribute("reservation") Reservation reservation) {
        reservationService.saveReservation(reservation);
        return "redirect:/admin/reservations";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.getReservationByID(id);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String formattedDate = reservation.getReservationDate().format(formatter);
        model.addAttribute("formattedDate", formattedDate);
        model.addAttribute("reservation", reservation);
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("tables", tableService.getAllTables());
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "admin/reservations/edit";
    }

    @PostMapping("/update/{id}")
    public String updateReservation(@PathVariable Long id, @ModelAttribute("reservation") Reservation reservation) {
        Reservation reservationByID = reservationService.getReservationByID(id);
        reservationByID.setReservationDate(reservation.getReservationDate());
        reservationByID.setNumberOfGuests(reservation.getNumberOfGuests());
        reservationByID.setStatus(reservation.getStatus());
        reservationByID.setRestaurant(reservation.getRestaurant());
        reservationByID.setCustomer(reservation.getCustomer());
        reservationByID.setTable(reservation.getTable());
        reservationService.saveReservation(reservationByID);
        return "redirect:/admin/reservations";
    }


    @GetMapping("/delete/{id}")
    public String deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return "redirect:/admin/reservations";
    }
}
