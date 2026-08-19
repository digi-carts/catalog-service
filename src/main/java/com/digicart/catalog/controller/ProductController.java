package com.digicart.catalog.controller;

import com.digicart.catalog.dto.ProductCreateRequest;
import com.digicart.catalog.dto.ProductUpdateRequest;
import com.digicart.catalog.dto.StockDeductRequest;
import com.digicart.catalog.entity.Product;
import com.digicart.catalog.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * REST controller exposing product HTTP APIs for <em>catalog-service</em>.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    /**
     * Creates a new {@code ProductController}.
     *
     * @param productService product service collaborator
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Handles GET.
     *
     * @param storeId store (tenant) identifier
     * @param search free-text search
     * @param tag product tag filter
     * @param category category filter
     * @param sort sort expression
     * @param page 1-based page index
     * @param limit page size
     * @return HTTP response
     */
    @GetMapping
    public ResponseEntity<?> list(
        @RequestHeader(value = "x-store-id", required = false) String storeId,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String tag,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String sort,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int limit
    ) {
        if (storeId == null || storeId.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "x-store-id required"));
        return ResponseEntity.ok(productService.findAll(storeId, search, tag, category, sort, page, limit));
    }

    /**
     * Handles {@code GET /stock-summary}.
     *
     * @param storeId store (tenant) identifier
     * @return HTTP response
     */
    @GetMapping("/stock-summary")
    public ResponseEntity<?> stockSummary(
        @RequestHeader(value = "x-store-id", required = false) String storeId
    ) {
        if (storeId == null || storeId.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "storeId required"));
        return ResponseEntity.ok(productService.stockSummary(storeId));
    }

    /**
     * Handles {@code GET /tags}.
     *
     * @param storeId store (tenant) identifier
     * @return HTTP response
     */
    @GetMapping("/tags")
    public ResponseEntity<?> tags(
        @RequestHeader(value = "x-store-id", required = false) String storeId
    ) {
        if (storeId == null || storeId.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "x-store-id required"));
        return ResponseEntity.ok(Map.of("tags", productService.findTags(storeId)));
    }

    /**
     * Handles {@code GET /{id}}.
     *
     * @param id resource identifier
     * @param storeId store (tenant) identifier
     * @return HTTP response
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(
        @PathVariable String id,
        @RequestHeader(value = "x-store-id", required = false) String storeId
    ) {
        if (storeId == null || storeId.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "x-store-id required"));
        return productService.findById(storeId, id)
            .<ResponseEntity<?>>map(p -> ResponseEntity.ok(Map.of("product", p)))
            .orElse(ResponseEntity.status(404).body(Map.of("error", "Product not found")));
    }

    /**
     * Handles POST.
     *
     * @param storeId store (tenant) identifier
     * @param userEmail caller email ({@code x-user-email})
     * @param req request payload
     * @return HTTP response
     */
    @PostMapping
    public ResponseEntity<?> create(
        @RequestHeader(value = "x-store-id", required = false) String storeId,
        @RequestHeader(value = "x-user-email", required = false) String userEmail,
        @Valid @RequestBody ProductCreateRequest req
    ) {
        if (storeId == null || storeId.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "x-store-id header required"));
        try {
            Product product = productService.create(storeId, userEmail, req);
            return ResponseEntity.status(201).body(Map.of("product", product));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Handles {@code PATCH /{id}}.
     *
     * @param id resource identifier
     * @param storeId store (tenant) identifier
     * @param req request payload
     * @return HTTP response
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
        @PathVariable UUID id,
        @RequestHeader(value = "x-store-id", required = false) String storeId,
        @RequestBody ProductUpdateRequest req
    ) {
        if (storeId == null || storeId.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "x-store-id header required"));
        try {
            Product product = productService.update(id, storeId, req);
            return ResponseEntity.ok(Map.of("product", product));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", "Product not found"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }
    }

    /**
     * Handles {@code DELETE /{id}}.
     *
     * @param id resource identifier
     * @param storeId store (tenant) identifier
     * @param role caller role
     * @return HTTP response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
        @PathVariable UUID id,
        @RequestHeader(value = "x-store-id", required = false) String storeId,
        @RequestHeader(value = "x-user-role", required = false) String role
    ) {
        try {
            productService.delete(id, storeId, role);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", "Product not found"));
        } catch (SecurityException | IllegalArgumentException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Handles {@code POST /deduct-stock}.
     *
     * @param req request payload
     * @return HTTP response
     */
    @PostMapping("/deduct-stock")
    public ResponseEntity<?> deductStock(@RequestBody StockDeductRequest req) {
        if (req.items() == null || req.items().isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "items required"));
        productService.deductStock(req);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    /**
     * Handles {@code POST /{id}/images-url}.
     *
     * @param id resource identifier
     * @param storeId store (tenant) identifier
     * @param body JSON request body
     * @return HTTP response
     */
    @PostMapping("/{id}/images-url")
    public ResponseEntity<?> addImageUrl(
        @PathVariable UUID id,
        @RequestHeader(value = "x-store-id", required = false) String storeId,
        @RequestBody Map<String, String> body
    ) {
        if (storeId == null || storeId.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "x-store-id header required"));
        String url = body.get("url");
        if (url == null || url.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "url required"));
        try {
            Product product = productService.addImageUrl(id, storeId, url);
            return ResponseEntity.ok(Map.of("product", product));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", "Product not found"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }
    }
}
