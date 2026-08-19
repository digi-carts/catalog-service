package com.digicart.catalog.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * Request/response DTO: Product Create Request.
 */
public record ProductCreateRequest(
    @NotBlank String name,
    String description,
    @Positive double price,
    @Min(0) int stock,
    String categoryId,
    List<String> tags,
    List<String> images,
    List<Spec> specs
) {}
