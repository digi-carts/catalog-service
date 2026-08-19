package com.digicart.catalog.dto;

import java.util.List;

/**
 * Request/response DTO: Product Update Request.
 */
public record ProductUpdateRequest(
    String name,
    String description,
    Double price,
    Integer stock,
    String categoryId,
    List<String> tags,
    List<String> images,
    List<Spec> specs
) {}
