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
    @Query("SELECT c FROM Category c WHERE c.storeId = :storeId AND c.name = :name AND c.parent.id = :parentId")
    Optional<Category> findByStoreIdAndNameAndParentId(@Param("storeId") String storeId, @Param("name") String name, @Param("parentId") UUID parentId);

    /**
     * Returns whether a category exists for the id and store.
     *
     * @param id resource identifier
     * @param storeId store (tenant) identifier
     * @return the boolean
     */
    boolean existsByIdAndStoreId(UUID id, String storeId);
}
