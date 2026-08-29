package com.digicart.catalog.repository;

import com.digicart.catalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for category  persistence.
 */
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByStoreIdOrderByNameAsc(String storeId);
    Optional<Category> findByStoreIdAndNameAndParentIsNull(String storeId, String name);

    @Query("SELECT c FROM Category c WHERE c.storeId = :storeId AND c.name = :name AND c.parent.id = :parentId")
    Optional<Category> findByStoreIdAndNameAndParentId(@Param("storeId") String storeId, @Param("name") String name, @Param("parentId") UUID parentId);

    boolean existsByIdAndStoreId(UUID id, String storeId);

    @Query("SELECT c.id, COUNT(p) FROM Category c LEFT JOIN c.products p WHERE c.storeId = :storeId GROUP BY c.id")
    List<Object[]> countProductsPerCategory(@Param("storeId") String storeId);
}
