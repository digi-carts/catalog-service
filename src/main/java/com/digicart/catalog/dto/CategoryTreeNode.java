package com.digicart.catalog.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record CategoryTreeNode(
    UUID id,
    String name,
    UUID parentId,
    long productCount,
    List<CategoryTreeNode> children
) {
    public CategoryTreeNode(UUID id, String name, UUID parentId, long productCount) {
        this(id, name, parentId, productCount, new ArrayList<>());
    }
}
