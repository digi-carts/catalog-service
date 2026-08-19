package com.digicart.catalog.dto;

import java.util.List;

/**
 * Request/response DTO: Stock Deduct Request.
 */
public record StockDeductRequest(List<StockItem> items) {
    /**
     * Immutable data record Stock Item.
     */
    public record StockItem(String productId, int qty) {}
}
