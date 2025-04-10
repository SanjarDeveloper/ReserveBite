package com.example.reservebite.controller.admin;

import com.example.reservebite.DTO.RestaurantFormDTO;
import com.example.reservebite.entity.*;
import com.example.reservebite.repository.*;
import com.example.reservebite.service.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/admin/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final CuisineService cuisineService;
    private final ReservationService reservationService;
    private final TableService tableService;
    private final MenuService menuService;
    private final OrderItemService orderItemService;
    private final OrderService orderService;

    private final RestaurantRepository restaurantRepository;
    private final ReservationRepository reservationRepository;
    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final MenuRepository menuRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MeasurementRepository measurementRepository;
    private final CuisineRepository cuisineRepository;

    public RestaurantController(RestaurantService restaurantService, CuisineService cuisineService, ReservationService reservationService, TableService tableService, MenuService menuService, OrderItemService orderItemService, OrderService orderService, RestaurantRepository restaurantRepository, ReservationRepository reservationRepository, OrderRepository orderRepository, TableRepository tableRepository, MenuRepository menuRepository, MenuCategoryRepository menuCategoryRepository, MeasurementRepository measurementRepository, CuisineRepository cuisineRepository) {
        this.restaurantService = restaurantService;
        this.cuisineService = cuisineService;
        this.reservationService = reservationService;
        this.tableService = tableService;
        this.menuService = menuService;
        this.orderItemService = orderItemService;
        this.orderService = orderService;
        this.restaurantRepository = restaurantRepository;
        this.reservationRepository = reservationRepository;
        this.orderRepository = orderRepository;
        this.tableRepository = tableRepository;
        this.menuRepository = menuRepository;
        this.menuCategoryRepository = menuCategoryRepository;
        this.measurementRepository = measurementRepository;
        this.cuisineRepository = cuisineRepository;
    }

    @GetMapping
    public String showRestaurantManagement(Model model) {
        // Total restaurants
        long totalRestaurants = restaurantRepository.count();
        model.addAttribute("totalRestaurants", totalRestaurants);

        // Active restaurants
        long activeRestaurants = restaurantRepository.countByIsActiveTrue();
        model.addAttribute("activeRestaurants", activeRestaurants);

        // Total orders
        long totalOrders = orderRepository.count();
        model.addAttribute("totalOrders", totalOrders);

        // Total reservations
        long totalReservations = reservationRepository.count();
        model.addAttribute("totalReservations", totalReservations);

        // Top performing restaurants
        List<Object[]> topRestaurants = restaurantRepository.findTopPerformingRestaurants();
        model.addAttribute("topRestaurants", topRestaurants);

        // Recent restaurants
        List<Restaurant> recentRestaurants = restaurantRepository.findTop5ByOrderByIdDesc();
        model.addAttribute("recentRestaurants", recentRestaurants != null ? recentRestaurants : Collections.emptyList());

        // Recent tables
        List<Table> recentTables = tableRepository.findTop5ByOrderByIdDesc();
        model.addAttribute("recentTables", recentTables != null ? recentTables : Collections.emptyList());

        // Recent menus
        List<Menu> recentMenus = menuRepository.findTop5ByOrderByIdDesc();
        model.addAttribute("recentMenus", recentMenus != null ? recentMenus : Collections.emptyList());

        // Recent menu categories
        List<MenuCategory> recentMenuCategories = menuCategoryRepository.findTop5ByOrderByIdDesc();
        model.addAttribute("recentMenuCategories", recentMenuCategories != null ? recentMenuCategories : Collections.emptyList());

        // Recent measurements
        List<Measurement> recentMeasurements = measurementRepository.findTop5ByOrderByIdDesc();
        model.addAttribute("recentMeasurements", recentMeasurements != null ? recentMeasurements : Collections.emptyList());

        // Recent cuisines
        List<Cuisine> recentCuisines = cuisineRepository.findTop5ByOrderByIdDesc();
        model.addAttribute("recentCuisines", recentCuisines != null ? recentCuisines : Collections.emptyList());

        return "admin/restaurants/dashboard";
    }

    @GetMapping("/all")
    public String showAllRestaurants(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {

        // Determine sort direction
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Fetch restaurants with search and sort
        List<Restaurant> restaurants;
        if (search != null && !search.trim().isEmpty()) {
            restaurants = restaurantRepository.findBySearch(search);
        } else {
            // Fetch all restaurants with cuisine eagerly loaded
            restaurants = restaurantRepository.findAllWithCuisine();
        }

        // Sort the list based on the sortBy parameter with null checks
        restaurants.sort((r1, r2) -> {
            int compare;
            switch (sortBy) {
                case "address":
                    String address1 = r1.getAddress() != null ? r1.getAddress() : "";
                    String address2 = r2.getAddress() != null ? r2.getAddress() : "";
                    compare = address1.compareToIgnoreCase(address2);
                    break;
                case "phone":
                    String phone1 = r1.getPhone() != null ? r1.getPhone() : "";
                    String phone2 = r2.getPhone() != null ? r2.getPhone() : "";
                    compare = phone1.compareToIgnoreCase(phone2);
                    break;
                case "email":
                    String email1 = r1.getEmail() != null ? r1.getEmail() : "";
                    String email2 = r2.getEmail() != null ? r2.getEmail() : "";
                    compare = email1.compareToIgnoreCase(email2);
                    break;
                case "totalTables":
                    Integer tables1 = r1.getTotalTables() != null ? r1.getTotalTables() : 0;
                    Integer tables2 = r2.getTotalTables() != null ? r2.getTotalTables() : 0;
                    compare = tables1.compareTo(tables2);
                    break;
                case "isActive":
                    Boolean active1 = r1.getIsActive() != null ? r1.getIsActive() : false;
                    Boolean active2 = r2.getIsActive() != null ? r2.getIsActive() : false;
                    compare = active1.compareTo(active2);
                    break;
                case "cuisine.name":
                    // Handle null cuisines
                    String cuisineName1 = r1.getCuisine() != null ? r1.getCuisine().getName() : "";
                    String cuisineName2 = r2.getCuisine() != null ? r2.getCuisine().getName() : "";
                    compare = cuisineName1.compareToIgnoreCase(cuisineName2);
                    break;
                case "name":
                default:
                    String name1 = r1.getName() != null ? r1.getName() : "";
                    String name2 = r2.getName() != null ? r2.getName() : "";
                    compare = name1.compareToIgnoreCase(name2);
                    break;
            }
            return direction == Sort.Direction.ASC ? compare : -compare;
        });

        // Add attributes to the model
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDir", sortDir);
        model.addAttribute("search", search);

        return "admin/restaurants/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("restaurantForm", new RestaurantFormDTO());
        model.addAttribute("cuisines", cuisineService.getAllActiveCuisines());
        return "admin/restaurants/create";
    }

    @PostMapping
    public String createRestaurant(@ModelAttribute("restaurantForm") RestaurantFormDTO restaurantForm, Model model) {
        if (restaurantForm.getCuisineId() == null) {
            model.addAttribute("error", "Please select a cuisine.");
            model.addAttribute("cuisines", cuisineService.getAllActiveCuisines());
            return "admin/restaurants/create";
        }

        Cuisine cuisine = cuisineService.getCuisineById(restaurantForm.getCuisineId());
        if (cuisine == null) {
            model.addAttribute("error", "Selected cuisine does not exist.");
            model.addAttribute("cuisines", cuisineService.getAllActiveCuisines());
            return "admin/restaurants/create";
        }

        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantForm.getName());
        restaurant.setAddress(restaurantForm.getAddress());
        restaurant.setPhone(restaurantForm.getPhone());
        restaurant.setEmail(restaurantForm.getEmail());
        restaurant.setTotalTables(restaurantForm.getTotalTables());
        restaurant.setIsActive(restaurantForm.getActive());
        restaurant.setCuisine(cuisine);
        restaurant.setCoordinates(restaurantForm.getCoordinates()); // Set coordinates

        restaurantService.saveRestaurant(restaurant);
        return "redirect:/admin/restaurants";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Restaurant restaurant = restaurantService.getRestaurantByID(id);
        RestaurantFormDTO restaurantForm = new RestaurantFormDTO();
        restaurantForm.setId(restaurant.getId());
        restaurantForm.setName(restaurant.getName());
        restaurantForm.setAddress(restaurant.getAddress());
        restaurantForm.setPhone(restaurant.getPhone());
        restaurantForm.setEmail(restaurant.getEmail());
        restaurantForm.setTotalTables(restaurant.getTotalTables());
        restaurantForm.setActive(restaurant.getIsActive());
        restaurantForm.setCoordinates(restaurant.getCoordinates()); // Populate coordinates
        if (restaurant.getCuisine() != null) {
            restaurantForm.setCuisineId(restaurant.getCuisine().getId());
        }
        model.addAttribute("restaurantForm", restaurantForm);
        model.addAttribute("cuisines", cuisineService.getAllActiveCuisines());
        return "admin/restaurants/edit";
    }

    @PostMapping("/update/{id}")
    public String updateRestaurant(@PathVariable Long id, @ModelAttribute("restaurantForm") RestaurantFormDTO restaurantForm, Model model) {
        if (restaurantForm.getCuisineId() == null) {
            model.addAttribute("error", "Please select a cuisine.");
            model.addAttribute("cuisines", cuisineService.getAllCuisines());
            return "admin/restaurants/edit";
        }

        Cuisine cuisine = cuisineService.getCuisineById(restaurantForm.getCuisineId());
        if (cuisine == null) {
            model.addAttribute("error", "Selected cuisine does not exist.");
            model.addAttribute("cuisines", cuisineService.getAllCuisines());
            return "admin/restaurants/edit";
        }

        Restaurant restaurant = restaurantService.getRestaurantByID(id);
        restaurant.setName(restaurantForm.getName());
        restaurant.setAddress(restaurantForm.getAddress());
        restaurant.setPhone(restaurantForm.getPhone());
        restaurant.setEmail(restaurantForm.getEmail());
        restaurant.setTotalTables(restaurantForm.getTotalTables());
        restaurant.setIsActive(restaurantForm.getActive());
        restaurant.setCuisine(cuisine);
        restaurant.setCoordinates(restaurantForm.getCoordinates()); // Update coordinates

        restaurantService.saveRestaurant(restaurant);
        return "redirect:/admin/restaurants";
    }

    @GetMapping("/delete/{id}")
    public String deleteRestaurant(@PathVariable Long id) {
        for (Menu allMenu : menuService.getAllMenus()) {
            if (allMenu.getRestaurant().getId().equals(id)) {
                for (OrderItem allOrderItem : orderItemService.getAllOrderItems()) {
                    if (allOrderItem.getMenu().getId().equals(allMenu.getId())) {
                        orderItemService.deleteOrderItem(allOrderItem.getId());
                    }
                }
                menuService.deleteMenu(allMenu.getId());
            }
        }
        for (Order allOrder : orderService.getAllOrders()) {
            if (allOrder.getRestaurant().getId().equals(id)) {
                orderService.deleteOrder(allOrder.getId());
            }
        }

        for (Reservation allReservation : reservationService.getAllReservations()) {
            if (allReservation.getRestaurant().getId().equals(id)) {
                reservationService.deleteReservation(allReservation.getId());
            }
        }
        for (Table allTable : tableService.getAllTables()) {
            if (allTable.getRestaurant().getId().equals(id)) {
                tableService.deleteTable(allTable.getId());
            }
        }

        restaurantService.deleteRestaurant(id);
        return "redirect:/admin/restaurants";
    }
}