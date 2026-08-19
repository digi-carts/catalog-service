package com.digicart.catalog.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Immutable data record Category Tree Node.
 */
public record CategoryTreeNode(
    UUID id,
    String name,
    UUID parentId,
    long productCount,
    List<CategoryTreeNode> children
) {
    /**
     * Creates a new {@code CategoryTreeNode}.
     *
     * @param id resource identifier
     * @param name name
     * @param parentId parent id
     * @param productCount product count
     */
    public CategoryTreeNode(UUID id, String name, UUID parentId, long productCount) {
        this(id, name, parentId, productCount, new ArrayList<>());
    }
}
