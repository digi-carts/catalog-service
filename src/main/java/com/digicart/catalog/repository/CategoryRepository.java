package com.digicart.catalog.repository;

import com.digicart.catalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for category  persistence.
 */
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByStoreIdOrderByNameAsc(String storeId);
    Optional<Category> findByStoreIdAndNameAndParentIsNull(String storeId, String name);
    Optional<Category> findByStoreIdAndNameAndParentId(String storeId, String name, UUID parentId);
    boolean existsByIdAndStoreId(UUID id, String storeId);
}
