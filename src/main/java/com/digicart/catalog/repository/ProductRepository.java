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
    List<Product> findFiltered(
        @Param("storeId") String storeId,
        @Param("search") String search,
        @Param("categoryIds") Set<UUID> categoryIds,
        Pageable pageable
    );

    @Query("SELECT COUNT(p) FROM Product p WHERE p.storeId = :storeId " +
           "AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:#{#categoryIds == null || #categoryIds.isEmpty()} = true OR p.category.id IN :categoryIds)")
    long countFiltered(
        @Param("storeId") String storeId,
        @Param("search") String search,
        @Param("categoryIds") Set<UUID> categoryIds
    );

    long countByStoreId(String storeId);
    long countByStoreIdAndStock(String storeId, int stock);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.storeId = :storeId AND p.stock > 0 AND p.stock <= :threshold")
    long countLowStock(@Param("storeId") String storeId, @Param("threshold") int threshold);

    @Query("SELECT p FROM Product p WHERE p.storeId = :storeId AND p.stock <= :threshold ORDER BY p.stock ASC")
    List<Product> findTopLowStock(@Param("storeId") String storeId, @Param("threshold") int threshold, Pageable pageable);

    Optional<Product> findByIdAndStoreId(UUID id, String storeId);

    @Query("SELECT p FROM Product p WHERE p.storeId = :storeId AND LOWER(p.name) = LOWER(:name)")
    Optional<Product> findByStoreIdAndNameIgnoreCase(@Param("storeId") String storeId, @Param("name") String name);

    @Query("SELECT p FROM Product p WHERE p.storeId = :storeId AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Optional<Product> findFirstByStoreIdAndNameContainingIgnoreCase(@Param("storeId") String storeId, @Param("name") String name, Pageable pageable);

    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :qty WHERE p.id = :id AND p.stock >= :qty")
    int deductStock(@Param("id") UUID id, @Param("qty") int qty);
}
