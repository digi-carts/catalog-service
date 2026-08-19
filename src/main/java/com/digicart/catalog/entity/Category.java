package com.digicart.catalog.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity mapped in this service schema (Category).
 */
@Entity
@Table(name = "category", schema = "catalog_svc")
@EntityListeners(AuditingEntityListener.class)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "store_id", nullable = false)
    private String storeId;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Category> children = new ArrayList<>();

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

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
     * Returns parent.
     * @return the category
     */
    public Category getParent() { return parent; }
    /**
     * Sets parent.
     *
     * @param parent parent
     */
    public void setParent(Category parent) { this.parent = parent; }
    /**
     * Returns children.
     * @return matching records
     */
    public List<Category> getChildren() { return children; }
    /**
     * Returns products.
     * @return matching records
     */
    public List<Product> getProducts() { return products; }
    /**
     * Returns created at.
     * @return the instant
     */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Returns parent id.
     * @return the uuid
     */
    public UUID getParentId() {
        return parent != null ? parent.getId() : null;
    }
}
