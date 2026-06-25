package com.example.inventoryservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    private String productId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Version
    private Long version; // optimistic locking to guard concurrent stock updates

    public Product() {}

    public Product(String productId, String name, Integer stockQuantity) {
        this.productId = productId;
        this.name = name;
        this.stockQuantity = stockQuantity;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
