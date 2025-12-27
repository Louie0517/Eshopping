package model;

import java.time.LocalDateTime;

public class InventoryOverview {

    private int id;
    private int productId;
    private int stock;
    private String name;
    private LocalDateTime updatedAt;

    // ===== Constructors =====
    public InventoryOverview() {}

    public InventoryOverview(int productId, String name, int stock) {
        this.productId = productId;
        this.name = name;
        this.stock = stock;
    }

    public InventoryOverview(int id, int productId, int stock, LocalDateTime updatedAt) {
        this.id = id;
        this.productId = productId;
        this.stock = stock;
        this.updatedAt = updatedAt;
    }

    // ===== Getters & Setters =====
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getName(){ return name;}
    public void setName(String name){this.name = name;}

    // ===== Business Logic =====
    public boolean isLowStock(int threshold) {
        return stock <= threshold;
    }
}
