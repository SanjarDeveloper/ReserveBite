package com.example.reservebite.DTO;

public class SlotDTO {
    private String time;
    private boolean available;

    public SlotDTO(String time, boolean available) {
        this.time = time;
        this.available = available;
    }

    public String getTime() {
        return time;
    }

    public boolean isAvailable() {
        return available;
    }
}
