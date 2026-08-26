package com.digicart.catalog.controller;

import com.digicart.catalog.dto.ProductCreateRequest;
import com.digicart.catalog.dto.ProductUpdateRequest;
import com.digicart.catalog.dto.StockDeductRequest;
import com.digicart.catalog.entity.Product;
import com.digicart.catalog.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller exposing product HTTP APIs for <em>catalog-service</em>.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

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

    @GetMapping("/stock-summary")
    public ResponseEntity<?> stockSummary(
        @RequestHeader(value = "x-store-id", required = false) String storeId
    ) {
        if (storeId == null || storeId.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "storeId required"));
        return ResponseEntity.ok(productService.stockSummary(storeId));
    }

    @GetMapping("/tags")
    public ResponseEntity<?> tags(
        @RequestHeader(value = "x-store-id", required = false) String storeId
    ) {
        if (storeId == null || storeId.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "x-store-id required"));
        return ResponseEntity.ok(Map.of("tags", productService.findTags(storeId)));
    }

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

    @PostMapping
    public ResponseEntity<?> create(
        @RequestHeader(value = "x-store-id", required = false) String storeId,
        @RequestHeader(value = "x-user-email", required = false) String userEmail,
        @RequestHeader(value = "x-user-role", required = false) String userRole,
        @Valid @RequestBody ProductCreateRequest req
    ) {
        if (!"merchant".equalsIgnoreCase(userRole) && !"superadmin".equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }
        if (storeId == null || storeId.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "x-store-id header required"));
        try {
            Product product = productService.create(storeId, userEmail, req);
            return ResponseEntity.status(201).body(Map.of("product", product));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<?> uploadImage(
        @PathVariable String id,
        @RequestHeader(value = "x-user-role", required = false) String userRole,
        @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        if (!"merchant".equalsIgnoreCase(userRole) && !"superadmin".equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid file type"));
        }
        String ct = file.getContentType();
        List<String> allowed = List.of("image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml");
        if (ct == null || !allowed.contains(ct)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid file type"));
        }
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload";
        return ResponseEntity.ok(Map.of("url", "/uploads/" + filename));
    }

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

    @PostMapping("/deduct-stock")
    public ResponseEntity<?> deductStock(@RequestBody StockDeductRequest req) {
        if (req.items() == null || req.items().isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "items required"));
        productService.deductStock(req);
        return ResponseEntity.ok(Map.of("ok", true));
    }

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
