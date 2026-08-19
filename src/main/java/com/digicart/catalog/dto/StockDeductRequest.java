package com.digicart.catalog.dto;

import java.util.List;

public record StockDeductRequest(List<StockItem> items) {
    public record StockItem(String productId, int qty) {}
}
