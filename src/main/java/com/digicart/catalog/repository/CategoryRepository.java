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
    /**
     * Finds by store id order by name asc.
     *
     * @param storeId store (tenant) identifier
     * @return matching records
     */
    List<Category> findByStoreIdOrderByNameAsc(String storeId);
    /**
     * Finds by store id and name and parent is null.
     *
     * @param storeId store (tenant) identifier
     * @param name name
     * @return the value if present
     */
    Optional<Category> findByStoreIdAndNameAndParentIsNull(String storeId, String name);
    /**
     * Finds by store id and name and parent id.
     *
     * @param storeId store (tenant) identifier
     * @param name name
     * @param parentId parent id
     * @return the value if present
     */
    Optional<Category> findByStoreIdAndNameAndParentId(String storeId, String name, UUID parentId);
    /**
     * Returns whether by id and store id exists.
     *
     * @param id resource identifier
     * @param storeId store (tenant) identifier
     * @return the boolean
     */
    boolean existsByIdAndStoreId(UUID id, String storeId);
}
