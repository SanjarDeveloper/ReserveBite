package com.example.reservebite.controller.admin;

import com.example.reservebite.DTO.MenuFormDTO;
import com.example.reservebite.entity.Menu;
import com.example.reservebite.entity.Restaurant;
import com.example.reservebite.entity.Measurement;
import com.example.reservebite.entity.MenuCategory;
import com.example.reservebite.repository.MenuRepository;
import com.example.reservebite.repository.RestaurantRepository;
import com.example.reservebite.repository.MeasurementRepository;
import com.example.reservebite.repository.MenuCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/menus")
public class MenusController {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final MeasurementRepository measurementRepository;
    private final MenuCategoryRepository menuCategoryRepository;

    @Autowired
    public MenusController(MenuRepository menuRepository, RestaurantRepository restaurantRepository,
                           MeasurementRepository measurementRepository, MenuCategoryRepository menuCategoryRepository) {
        this.menuRepository = menuRepository;
        this.restaurantRepository = restaurantRepository;
        this.measurementRepository = measurementRepository;
        this.menuCategoryRepository = menuCategoryRepository;
    }

    @GetMapping
    public String showMenuList(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {

        // Determine sort direction
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);

        // Fetch menus with search and sort
        List<Menu> menus;
        if (search != null && !search.trim().isEmpty()) {
            menus = menuRepository.findByFoodNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrRestaurantNameContainingIgnoreCaseOrMeasurementNameContainingIgnoreCaseOrMenuCategoryNameContainingIgnoreCase(
                    search, search, search, search, search, sort);
        } else {
            menus = menuRepository.findAll(sort);
        }

        model.addAttribute("menus", menus);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("search", search);

        return "admin/menus/list";
    }

    @GetMapping("/new")
    public String showCreateMenuForm(Model model) {
        model.addAttribute("menuForm", new MenuFormDTO());
        model.addAttribute("restaurants", restaurantRepository.findAll());
        model.addAttribute("measurements", measurementRepository.findAll());
        model.addAttribute("menuCategories", menuCategoryRepository.findAll());
        return "admin/menus/create";
    }

    @PostMapping
    public String createMenu(@ModelAttribute("menuForm") MenuFormDTO menuForm, Model model) {
        try {
            // Log the form data
            System.out.println("Received MenuFormDTO: " +
                    "foodName=" + menuForm.getFoodName() +
                    ", price=" + menuForm.getPrice() +
                    ", quantity=" + menuForm.getQuantity() +
                    ", description=" + menuForm.getDescription() +
                    ", isActive=" + menuForm.getIsActive() +
                    ", restaurantId=" + menuForm.getRestaurantId() +
                    ", measurementId=" + menuForm.getMeasurementId() +
                    ", menuCategoryId=" + menuForm.getMenuCategoryId() +
                    ", image=" + (menuForm.getImage() != null ? menuForm.getImage().getOriginalFilename() : "null"));

            // Validate image (optional)
            if (menuForm.getImage() != null && !menuForm.getImage().isEmpty()) {
                System.out.println("Image content type: " + menuForm.getImage().getContentType());
                System.out.println("Image size: " + menuForm.getImage().getSize() + " bytes");
                if (!menuForm.getImage().getContentType().startsWith("image/")) {
                    model.addAttribute("error", "Please upload a valid image file (e.g., PNG, JPEG).");
                    model.addAttribute("restaurants", restaurantRepository.findAll());
                    model.addAttribute("measurements", measurementRepository.findAll());
                    model.addAttribute("menuCategories", menuCategoryRepository.findAll());
                    return "admin/menus/create";
                }
                if (menuForm.getImage().getSize() > 5 * 1024 * 1024) { // 5MB limit
                    model.addAttribute("error", "Image size must be less than 5MB.");
                    model.addAttribute("restaurants", restaurantRepository.findAll());
                    model.addAttribute("measurements", measurementRepository.findAll());
                    model.addAttribute("menuCategories", menuCategoryRepository.findAll());
                    return "admin/menus/create";
                }
            } else {
                System.out.println("No image uploaded in the form.");
            }

            Menu menu = new Menu();
            menu.setFoodName(menuForm.getFoodName());
            menu.setPrice(menuForm.getPrice());
            menu.setQuantity(menuForm.getQuantity());
            menu.setDescription(menuForm.getDescription());
            menu.setIsActive(menuForm.getIsActive() != null ? menuForm.getIsActive() : false);

            // Set relationships
            Restaurant restaurant = restaurantRepository.findById(menuForm.getRestaurantId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid restaurant ID: " + menuForm.getRestaurantId()));
            Measurement measurement = measurementRepository.findById(menuForm.getMeasurementId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid measurement ID: " + menuForm.getMeasurementId()));
            MenuCategory menuCategory = menuCategoryRepository.findById(menuForm.getMenuCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu category ID: " + menuForm.getMenuCategoryId()));
            menu.setRestaurant(restaurant);
            menu.setMeasurement(measurement);
            menu.setMenuCategory(menuCategory);

            // Handle image upload
            if (menuForm.getImage() != null && !menuForm.getImage().isEmpty()) {
                byte[] imageData = menuForm.getImage().getBytes();
                System.out.println("Setting imageData with length: " + (imageData != null ? imageData.length : "null"));
                menu.setImageData(imageData);
            } else {
                System.out.println("No image uploaded, setting imageData to null");
                menu.setImageData(null);
            }

            // Log the Menu entity before saving
            System.out.println("Menu entity before save: " +
                    "foodName=" + menu.getFoodName() +
                    ", imageData=" + (menu.getImageData() != null ? "byte[" + menu.getImageData().length + "]" : "null"));

            menuRepository.save(menu);
            return "redirect:/admin/menus";
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            model.addAttribute("error", "Error creating menu: " + e.getMessage());
            model.addAttribute("restaurants", restaurantRepository.findAll());
            model.addAttribute("measurements", measurementRepository.findAll());
            model.addAttribute("menuCategories", menuCategoryRepository.findAll());
            return "admin/menus/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditMenuForm(@PathVariable Long id, Model model) {
        Menu menu = menuRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid menu ID"));
        MenuFormDTO menuForm = new MenuFormDTO();
        menuForm.setId(menu.getId());
        menuForm.setFoodName(menu.getFoodName());
        menuForm.setPrice(menu.getPrice());
        menuForm.setQuantity(menu.getQuantity());
        menuForm.setDescription(menu.getDescription());
        menuForm.setIsActive(menu.getIsActive());
        menuForm.setRestaurantId(menu.getRestaurant() != null ? menu.getRestaurant().getId() : null);
        menuForm.setMeasurementId(menu.getMeasurement() != null ? menu.getMeasurement().getId() : null);
        menuForm.setMenuCategoryId(menu.getMenuCategory() != null ? menu.getMenuCategory().getId() : null);

        model.addAttribute("menuForm", menuForm);
        model.addAttribute("menu", menu);
        model.addAttribute("restaurants", restaurantRepository.findAll());
        model.addAttribute("measurements", measurementRepository.findAll());
        model.addAttribute("menuCategories", menuCategoryRepository.findAll());
        return "admin/menus/edit";
    }

    @PostMapping("/{id}")
    public String updateMenu(@PathVariable Long id, @ModelAttribute("menuForm") MenuFormDTO menuForm, Model model) throws IOException {
        try {
            // Validate image (optional)
            if (menuForm.getImage() != null && !menuForm.getImage().isEmpty()) {
                if (!menuForm.getImage().getContentType().startsWith("image/")) {
                    model.addAttribute("error", "Please upload a valid image file (e.g., PNG, JPEG).");
                    model.addAttribute("restaurants", restaurantRepository.findAll());
                    model.addAttribute("measurements", measurementRepository.findAll());
                    model.addAttribute("menuCategories", menuCategoryRepository.findAll());
                    return "admin/menus/edit";
                }
                if (menuForm.getImage().getSize() > 5 * 1024 * 1024) { // 5MB limit
                    model.addAttribute("error", "Image size must be less than 5MB.");
                    model.addAttribute("restaurants", restaurantRepository.findAll());
                    model.addAttribute("measurements", measurementRepository.findAll());
                    model.addAttribute("menuCategories", menuCategoryRepository.findAll());
                    return "admin/menus/edit";
                }
            }

            Menu menu = menuRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid menu ID"));
            menu.setFoodName(menuForm.getFoodName());
            menu.setPrice(menuForm.getPrice());
            menu.setQuantity(menuForm.getQuantity());
            menu.setDescription(menuForm.getDescription());
            menu.setIsActive(menuForm.getIsActive() != null ? menuForm.getIsActive() : false);

            // Set relationships
            Restaurant restaurant = restaurantRepository.findById(menuForm.getRestaurantId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid restaurant ID"));
            Measurement measurement = measurementRepository.findById(menuForm.getMeasurementId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid measurement ID"));
            MenuCategory menuCategory = menuCategoryRepository.findById(menuForm.getMenuCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu category ID"));
            menu.setRestaurant(restaurant);
            menu.setMeasurement(measurement);
            menu.setMenuCategory(menuCategory);

            // Handle image upload (only if a new image is provided)
            if (menuForm.getImage() != null && !menuForm.getImage().isEmpty()) {
                byte[] imageData = menuForm.getImage().getBytes();
                System.out.println("Updating imageData with length: " + (imageData != null ? imageData.length : "null"));
                menu.setImageData(imageData);
            } else {
                System.out.println("No new image uploaded, retaining existing imageData");
            }

            menuRepository.save(menu);
            return "redirect:/admin/menus";
        } catch (Exception e) {
            model.addAttribute("error", "Error updating menu: " + e.getMessage());
            model.addAttribute("restaurants", restaurantRepository.findAll());
            model.addAttribute("measurements", measurementRepository.findAll());
            model.addAttribute("menuCategories", menuCategoryRepository.findAll());
            return "admin/menus/edit";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteMenu(@PathVariable Long id) {
        Menu menu = menuRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid menu ID"));
        menuRepository.delete(menu);
        return "redirect:/admin/menus";
    }

    @GetMapping(value = "/image/{id}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    @ResponseBody
    public byte[] getImage(@PathVariable Long id) {
        Menu menu = menuRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid menu ID"));
        return menu.getImageData() != null ? menu.getImageData() : new byte[0];
    }
}