package com.example.reservebite.DTO;

import org.springframework.web.multipart.MultipartFile;

public class MenuFormDTO {
    private Long id;
    private String foodName;
    private Double price;
    private Integer quantity;
    private String description;
    private Boolean isActive;
    private Long restaurantId;
    private Long measurementId;
    private Long menuCategoryId;
    private MultipartFile image; // Field to hold the uploaded image

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Long getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(Long measurementId) {
        this.measurementId = measurementId;
    }

    public Long getMenuCategoryId() {
        return menuCategoryId;
    }

    public void setMenuCategoryId(Long menuCategoryId) {
        this.menuCategoryId = menuCategoryId;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}