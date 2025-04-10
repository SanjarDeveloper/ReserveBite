package com.example.reservebite.projections;

import java.math.BigDecimal;

public interface TopSellingItemProjection {
    String getName();
    int getOrders();
    BigDecimal getRevenue();
}
