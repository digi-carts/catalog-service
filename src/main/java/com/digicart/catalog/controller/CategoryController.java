package com.digicart.catalog.controller;

import com.digicart.catalog.dto.CategoryRequest;
import com.digicart.catalog.entity.Category;
import com.digicart.catalog.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller exposing category HTTP APIs for <em>catalog-service</em>.
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Creates a new {@code CategoryController}.
     *
     * @param categoryService category service collaborator
     */
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Handles GET.
     *
     * @param storeId store (tenant) identifier
     * @return HTTP response
     */
    @GetMapping
    public ResponseEntity<?> list(@RequestHeader(value = "x-store-id", required = false) String storeId) {
        if (storeId == null || storeId.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "x-store-id required"));

        List<Category> categories = categoryService.findByStore(storeId);
        var tree = categoryService.buildTree(categories);
        return ResponseEntity.ok(Map.of("categories", categories, "tree", tree));
    }

    /**
     * Handles POST.
     *
     * @param storeId store (tenant) identifier
     * @param req request payload
     * @return HTTP response
     */
    @PostMapping
    public ResponseEntity<?> create(
        @RequestHeader(value = "x-store-id", required = false) String storeId,
        @Valid @RequestBody CategoryRequest req
    ) {
        if (storeId == null || storeId.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "x-store-id header required"));

        Category category = categoryService.create(storeId, req);
        return ResponseEntity.status(201).body(Map.of("category", category));
    }

    /**
     * Handles {@code DELETE /{id}}.
     *
     * @param id resource identifier
     * @param storeId store (tenant) identifier
     * @return HTTP response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
        @PathVariable UUID id,
        @RequestHeader(value = "x-store-id", required = false) String storeId
    ) {
        if (storeId == null || storeId.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "x-store-id header required"));
        try {
            categoryService.delete(id, storeId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", "Category not found"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }
    }
}
