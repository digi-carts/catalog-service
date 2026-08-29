package com.digicart.catalog.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request/response DTO: Category Request.
 */
public record CategoryRequest(
    @NotBlank String name,
    String parentId
) {}
