package com.digicart.catalog.service;

import com.digicart.catalog.dto.CategoryRequest;
import com.digicart.catalog.dto.CategoryTreeNode;
import com.digicart.catalog.entity.Category;
import com.digicart.catalog.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Application service implementing category use cases for <em>catalog-service</em>.
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findByStore(String storeId) {
        return categoryRepository.findByStoreIdOrderByNameAsc(storeId);
    }

    @Transactional(readOnly = true)
    public List<CategoryTreeNode> buildTree(String storeId, List<Category> categories) {
        Map<UUID, Long> counts = categoryRepository.countProductsPerCategory(storeId)
                .stream()
                .collect(Collectors.toMap(r -> (UUID) r[0], r -> ((Number) r[1]).longValue()));

        Map<UUID, CategoryTreeNode> map = new LinkedHashMap<>();
        for (Category c : categories) {
            long count = counts.getOrDefault(c.getId(), 0L);
            map.put(c.getId(), new CategoryTreeNode(c.getId(), c.getName(), c.getParentId(), count));
        }
        List<CategoryTreeNode> roots = new ArrayList<>();
        for (CategoryTreeNode node : map.values()) {
            if (node.parentId() != null && map.containsKey(node.parentId())) {
                map.get(node.parentId()).children().add(node);
            } else {
                roots.add(node);
            }
        }
        return roots;
    }

    @Transactional
    public Category create(String storeId, CategoryRequest req) {
        UUID parentId = req.parentId() != null && !req.parentId().isBlank()
            ? UUID.fromString(req.parentId()) : null;

        // Return existing if duplicate
        Optional<Category> existing = parentId == null
            ? categoryRepository.findByStoreIdAndNameAndParentIsNull(storeId, req.name())
            : categoryRepository.findByStoreIdAndNameAndParentId(storeId, req.name(), parentId);
        if (existing.isPresent()) return existing.get();

        Category category = new Category();
        category.setStoreId(storeId);
        category.setName(req.name());
        if (parentId != null) {
            categoryRepository.findById(parentId).ifPresent(category::setParent);
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(UUID id, String storeId) {
        Category cat = categoryRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Category not found"));
        if (!cat.getStoreId().equals(storeId)) throw new SecurityException("Forbidden");

        // Reassign children to grandparent
        for (Category child : cat.getChildren()) {
            child.setParent(cat.getParent());
        }
        categoryRepository.delete(cat);
    }
}
