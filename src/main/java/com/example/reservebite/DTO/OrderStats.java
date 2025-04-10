package com.example.reservebite.DTO;

import java.time.LocalDate;
import java.sql.Date;

public class OrderStats {
    private LocalDate date;
    private Long orderCount;

    public OrderStats(Date date, Long orderCount) {
        this.date = date.toLocalDate();
        this.orderCount = orderCount;
    }

    public LocalDate getDate() {
        return date;
    }

    public Long getOrderCount() {
        return orderCount;
    }
}