package com.digicart.catalog.repository;

import com.digicart.catalog.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Spring Data JPA repository for product  persistence.
 */
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.storeId = :storeId " +
           "AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:#{#categoryIds == null || #categoryIds.isEmpty()} = true OR p.category.id IN :categoryIds)")
    /**
     * Finds filtered.
     *
     * @param storeId store (tenant) identifier
     * @param search free-text search
     * @param categoryIds category ids
     * @param pageable pageable
     * @return matching records
     */
    List<Product> findFiltered(
        @Param("storeId") String storeId,
        @Param("search") String search,
        @Param("categoryIds") Set<UUID> categoryIds,
        Pageable pageable
    );

    @Query("SELECT COUNT(p) FROM Product p WHERE p.storeId = :storeId " +
           "AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:#{#categoryIds == null || #categoryIds.isEmpty()} = true OR p.category.id IN :categoryIds)")
    /**
     * Count filtered.
     *
     * @param storeId store (tenant) identifier
     * @param search free-text search
     * @param categoryIds category ids
     * @return the long
     */
    long countFiltered(
        @Param("storeId") String storeId,
        @Param("search") String search,
        @Param("categoryIds") Set<UUID> categoryIds
    );
    /**
     * Count by store id.
     *
     * @param storeId store (tenant) identifier
     * @return the long
     */
    long countByStoreId(String storeId);
    /**
     * Count by store id and stock.
     *
     * @param storeId store (tenant) identifier
     * @param stock stock
     * @return the long
     */
    long countByStoreIdAndStock(String storeId, int stock);
    /**
     * Count low stock.
     *
     * @param storeId store (tenant) identifier
     * @param threshold threshold
     * @return the long
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.storeId = :storeId AND p.stock > 0 AND p.stock <= :threshold")
    long countLowStock(@Param("storeId") String storeId, @Param("threshold") int threshold);
    /**
     * Finds top low stock.
     *
     * @param storeId store (tenant) identifier
     * @param threshold threshold
     * @param pageable pageable
     * @return matching records
     */
    @Query("SELECT p FROM Product p WHERE p.storeId = :storeId AND p.stock <= :threshold ORDER BY p.stock ASC")
    List<Product> findTopLowStock(@Param("storeId") String storeId, @Param("threshold") int threshold, Pageable pageable);
    /**
     * Finds by id and store id.
     *
     * @param id resource identifier
     * @param storeId store (tenant) identifier
     * @return the value if present
     */
    Optional<Product> findByIdAndStoreId(UUID id, String storeId);
    /**
     * Finds by store id and name ignore case.
     *
     * @param storeId store (tenant) identifier
     * @param name name
     * @return the value if present
     */
    @Query("SELECT p FROM Product p WHERE p.storeId = :storeId AND LOWER(p.name) = LOWER(:name)")
    Optional<Product> findByStoreIdAndNameIgnoreCase(@Param("storeId") String storeId, @Param("name") String name);
    /**
     * Finds first by store id and name containing ignore case.
     *
     * @param storeId store (tenant) identifier
     * @param name name
     * @param pageable pageable
     * @return the value if present
     */
    @Query("SELECT p FROM Product p WHERE p.storeId = :storeId AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Optional<Product> findFirstByStoreIdAndNameContainingIgnoreCase(@Param("storeId") String storeId, @Param("name") String name, Pageable pageable);
    /**
     * Deduct stock.
     *
     * @param id resource identifier
     * @param qty qty
     * @return the int
     */
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :qty WHERE p.id = :id AND p.stock >= :qty")
    int deductStock(@Param("id") UUID id, @Param("qty") int qty);
}
