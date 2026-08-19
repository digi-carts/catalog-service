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

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestHeader(value = "x-store-id", required = false) String storeId) {
        if (storeId == null || storeId.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "x-store-id required"));

        List<Category> categories = categoryService.findByStore(storeId);
        var tree = categoryService.buildTree(categories);
        return ResponseEntity.ok(Map.of("categories", categories, "tree", tree));
    }

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
