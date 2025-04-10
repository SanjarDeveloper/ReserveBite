package com.example.reservebite.entity.enums;

public enum FulfillmentType {
    DELIVERY,
    DINE_IN,
    TAKEOUT;

    // Optional: Add a method to get the display name if needed
    public String getDisplayName() {
        return name().replace("_", " ").toLowerCase();
    }
}
