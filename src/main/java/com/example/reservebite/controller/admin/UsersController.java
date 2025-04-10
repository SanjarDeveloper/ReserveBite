package com.example.reservebite.controller.admin;

import com.example.reservebite.DTO.ActiveUserDTO;
import com.example.reservebite.entity.*;
import com.example.reservebite.repository.*;
import com.example.reservebite.service.UsersService;
import com.example.reservebite.service.WaiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/users")
public class UsersController {
    private final UsersService usersService;
    private final RoleRepository roleRepository;
    private final UsersRepository userRepository;
    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;
    private final WaiterRepository waiterRepository;
    private final WaiterService waiterService;
    private final DeliveryPersonRepository courierRepository;
    private final RestaurantRepository restaurantRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UsersController(UsersService usersService, RoleRepository roleRepository, UsersRepository userRepository, OrderRepository orderRepository, ReservationRepository reservationRepository, WaiterRepository waiterRepository, WaiterService waiterService, DeliveryPersonRepository courierRepository, RestaurantRepository restaurantRepository, PasswordEncoder passwordEncoder) {
        this.usersService = usersService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.reservationRepository = reservationRepository;
        this.waiterRepository = waiterRepository;
        this.waiterService = waiterService;
        this.courierRepository = courierRepository;
        this.restaurantRepository = restaurantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String showUserManagement(Model model) {
        // Total users
        long totalUsers = userRepository.count();
        model.addAttribute("totalUsers", totalUsers);

        // Active users
        long activeUsers = userRepository.countByIsActiveTrue();
        model.addAttribute("activeUsers", activeUsers);

        // Total orders by users
        long totalUserOrders = orderRepository.count();
        model.addAttribute("totalUserOrders", totalUserOrders);

        // Total reservations by users
        long totalUserReservations = reservationRepository.count();
        model.addAttribute("totalUserReservations", totalUserReservations);

        // Most active users (top 5 by order count)
        List<Object[]> topUsers = orderRepository.findTop5UsersByOrderCount();
        List<ActiveUserDTO> activeUsersList;

        if (topUsers == null || topUsers.isEmpty()) {
            activeUsersList = Collections.emptyList();
        } else {
            // Extract user IDs
            List<Long> userIds = topUsers.stream()
                    .map(result -> result[0] instanceof Number ? ((Number) result[0]).longValue() : null)
                    .filter(id -> id != null)
                    .collect(Collectors.toList());

            // Fetch all users in a single query
            Map<Long, Users> usersMap = userRepository.findAllById(userIds).stream()
                    .collect(Collectors.toMap(Users::getId, user -> user));

            // Map to ActiveUserDTO
            activeUsersList = topUsers.stream()
                    .map(result -> {
                        if (result == null || result.length < 2) {
                            return new ActiveUserDTO("N/A", "N/A", 0);
                        }

                        Long userId = result[0] instanceof Number ? ((Number) result[0]).longValue() : null;
                        Long orderCount = result[1] instanceof Number ? ((Number) result[1]).longValue() : 0L;

                        Users user = userId != null ? usersMap.get(userId) : null;

                        return new ActiveUserDTO(
                                user != null && user.getName() != null ? user.getName() : "N/A",
                                user != null && user.getEmail() != null ? user.getEmail() : "N/A",
                                orderCount != null ? orderCount : 0
                        );
                    })
                    .collect(Collectors.toList());
        }
        model.addAttribute("activeUsersList", activeUsersList);

        // Recent users (top 5 by ID, descending)
        List<Users> recentUsers = userRepository.findTop5ByOrderByIdDesc();
        model.addAttribute("recentUsers", recentUsers != null ? recentUsers : Collections.emptyList());

        return "admin/users/dashboard";
    }

    @GetMapping("/list")
    public String showUserList(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {

        // Determine sort direction
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);

        // Fetch users with search and sort
        List<Users> users;
        if (search != null && !search.trim().isEmpty()) {
            users = userRepository.findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCase(
                    search, search, search, search, sort);
        } else {
            users = userRepository.findAll(sort);
        }

        model.addAttribute("users", users);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("search", search);

        return "admin/users/list";
    }

    @GetMapping("/new")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new Users());
        model.addAttribute("roles", roleRepository.findActiveRoles());
        model.addAttribute("restaurants", restaurantRepository.findAll());
//        model.addAttribute("customer", new Customer());
        return "admin/users/create";
    }

    @PostMapping
    public String createUser(@ModelAttribute("user") Users user, @RequestParam("roles") Set<Long> roleIds, @RequestParam(value = "restaurantId", required = false) Long restaurantId) {
        // Fetch the roles from the database based on the provided roleIds
        Set<Role> selectedRoles = new HashSet<>();
        for (Long roleId : roleIds) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));
            selectedRoles.add(role);
        }

        // Check the roles and perform specific actions
        boolean hasWaiterRole = selectedRoles.stream().anyMatch(role -> role.getName().equals("ROLE_WAITER"));
        boolean hasAdminRole = selectedRoles.stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        boolean hasUserRole = selectedRoles.stream().anyMatch(role -> role.getName().equals("ROLE_USER"));
        boolean hasCourierRole = selectedRoles.stream().anyMatch(role -> role.getName().equals("ROLE_COURIER"));

        if (hasWaiterRole) {
            System.out.println("User has ROLE_WAITER");
            if (restaurantId == null) {
                throw new RuntimeException("Restaurant is required for ROLE_WAITER");
            }
            // Create a Waiter entity for this user
            Waiter waiter = new Waiter();
            Users savedUser = usersService.saveUser(user);
            usersService.updatePassword(savedUser, user.getPassword());
            waiter.setUser(savedUser);
            Restaurant restaurant = restaurantRepository.findById(restaurantId)
                    .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + restaurantId));
            waiter.setRestaurant(restaurant);
            // You might need to set the restaurant for the waiter; this depends on your form data
            // For now, we'll assume the restaurant is set elsewhere or fetched from the form
            waiterRepository.save(waiter);

        } else if (hasAdminRole) {
            System.out.println("User has ROLE_ADMIN");
            // Perform admin-specific actions (e.g., log the creation of an admin user)
            // No additional entity creation is typically needed for admins
            Users savedUser = usersService.saveUser(user);
            usersService.updatePassword(savedUser, user.getPassword());
            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            if (adminRole == null) {
                Role newRole = new Role();
                newRole.setName("ROLE_ADMIN");
                newRole.setActive(true);
                roleRepository.save(newRole);
            }
            roleIds.add(adminRole.getId());
            // Assign the roles to the user
            usersService.assignRolesToUser(savedUser.getId(), roleIds);

        } else if (hasCourierRole) {
            System.out.println("User has ROLE_COURIER");
            // Create a Courier entity for this user (if applicable)
            // Assuming you have a Courier entity similar to Waiter
            DeliveryPerson courier = new DeliveryPerson();
            courier.setName(user.getName());
            courier.setUsername(user.getUsername());
            courier.setPhone(user.getPhone());
            courier.setPassword(passwordEncoder.encode(user.getPassword()));
            courier.setEmail(user.getEmail());
            courier.setBalance(user.getBalance());
            courier.setRoles(user.getRoles());
            courier.setIsActive(user.getIsActive());
            courierRepository.save(courier);
        } else if (hasUserRole) {
            System.out.println("User has ROLE_USER");
            // Perform user-specific actions (e.g., log the creation of a regular user)
            // No additional entity creation is typically needed for regular users
            Users savedUser = usersService.saveUser(user);
            usersService.updatePassword(savedUser, user.getPassword());
            Role userRole = roleRepository.findByName("ROLE_USER");
            if (userRole == null) {
                    Role newRole = new Role();
                    newRole.setName("ROLE_USER");
                    newRole.setActive(true);
                    roleRepository.save(newRole);
            }
            roleIds.add(userRole.getId());
            // Assign the roles to the user
            usersService.assignRolesToUser(savedUser.getId(), roleIds);

        } else {
            System.out.println("No specific role assigned");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        Users user = usersService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/users/edit";
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") Users user, @RequestParam("roles") Set<Long> roleIds) {
        Users userById = usersService.getUserById(id);
        userById.setName(user.getName());
        userById.setUsername(user.getUsername());
        userById.setEmail(user.getEmail());
        userById.setPhone(user.getPhone());
        userById.setBalance(user.getBalance());
        userById.setIsActive(user.getIsActive());
        Set<Role> roles = roleIds.stream()
                .map(roleRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        userById.setRoles(roles);
        usersService.saveUser(userById);
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        usersService.deleteUser(id);
        return "redirect:/admin/users";
    }
}