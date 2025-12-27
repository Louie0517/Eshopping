package model;

import java.time.LocalDate;

public class Product {

    private int id;
    private String productName;
    private int categoryId;
    private int brandId;
    private int supplierId;
    private String unit;
    private double price;
    private int stock;

    private LocalDate dateAdded;
    private LocalDate updatedAt;

    // Display-only fields
    private String categoryName;
    private String brandName;
    private String supplierName;
    private String description;
    private String imagePath;
    private double markUp;
    private double sellingPrice;

    public Product() {}

    // ===== CORE GETTERS / SETTERS =====

    public int getId() { return id; }
    public void setId(int id) { this.id = id;}

    public int getStock(){ return stock;}
    public void setStock(int stock){ this.stock = stock;}

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public int getBrandId() { return brandId; }
    public void setBrandId(int brandId) { this.brandId = brandId; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public LocalDate getDateAdded() { return dateAdded; }
    public void setDateAdded(LocalDate dateAdded) { this.dateAdded = dateAdded; }

    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }

    // ===== DISPLAY / JOINED FIELDS =====

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getMarkUp() { return markUp; }
    public void setMarkUp(double markUp) { this.markUp = markUp; }

    public double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(double sellingPrice) { this.sellingPrice = sellingPrice; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    @Override
    public String toString() {
        return productName;
    }
}
