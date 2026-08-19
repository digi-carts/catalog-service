package com.digicart.catalog.entity;

import com.digicart.catalog.dto.Spec;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity mapped in this service schema (Product).
 */
@Entity
@Table(name = "product", schema = "catalog_svc")
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "store_id", nullable = false)
    private String storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer stock = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<String> images = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<String> tags = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<Spec> specs = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    /**
     * Returns id.
     * @return the uuid
     */
    public UUID getId() { return id; }
    /**
     * Returns store id.
     * @return the string
     */
    public String getStoreId() { return storeId; }
    /**
     * Sets store id.
     *
     * @param storeId store (tenant) identifier
     */
    public void setStoreId(String storeId) { this.storeId = storeId; }
    /**
     * Returns category.
     * @return the category
     */
    public Category getCategory() { return category; }
    /**
     * Sets category.
     *
     * @param category category filter
     */
    public void setCategory(Category category) { this.category = category; }
    /**
     * Returns category id.
     * @return the uuid
     */
    public UUID getCategoryId() { return category != null ? category.getId() : null; }
    /**
     * Returns name.
     * @return the string
     */
    public String getName() { return name; }
    /**
     * Sets name.
     *
     * @param name name
     */
    public void setName(String name) { this.name = name; }
    /**
     * Returns description.
     * @return the string
     */
    public String getDescription() { return description; }
    /**
     * Sets description.
     *
     * @param description description
     */
    public void setDescription(String description) { this.description = description; }
    /**
     * Returns price.
     * @return the double
     */
    public Double getPrice() { return price; }
    /**
     * Sets price.
     *
     * @param price price
     */
    public void setPrice(Double price) { this.price = price; }
    /**
     * Returns stock.
     * @return the integer
     */
    public Integer getStock() { return stock; }
    /**
     * Sets stock.
     *
     * @param stock stock
     */
    public void setStock(Integer stock) { this.stock = stock; }
    /**
     * Returns images.
     * @return matching records
     */
    public List<String> getImages() { return images; }
    /**
     * Sets images.
     *
     * @param images images
     */
    public void setImages(List<String> images) { this.images = images; }
    /**
     * Returns tags.
     * @return matching records
     */
    public List<String> getTags() { return tags; }
    /**
     * Sets tags.
     *
     * @param tags tags
     */
    public void setTags(List<String> tags) { this.tags = tags; }
    /**
     * Returns specs.
     * @return matching records
     */
    public List<Spec> getSpecs() { return specs; }
    /**
     * Sets specs.
     *
     * @param specs specs
     */
    public void setSpecs(List<Spec> specs) { this.specs = specs; }
    /**
     * Returns created at.
     * @return the instant
     */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Returns updated at.
     * @return the instant
     */
    public Instant getUpdatedAt() { return updatedAt; }
}
