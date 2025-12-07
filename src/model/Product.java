package model;

import java.time.LocalDate;

public class Product {
    private int id;
    private String name;
    private int categoryId;
    private int brandId;
    private int supplierId;
    private String unit;
    private double price;
    private int quantity;
    private LocalDate dateAdded;

    // new added 
    private int stock;
    private String description;
    private String imagePath;
    private LocalDate updatedAt;

    public Product() {}

    public Product(int id, String name, int categoryId, int brandId, int supplierId,
                   String unit, double price, int quantity, int stock, String description, String imagePath,LocalDate dateAdded, LocalDate updatedAt) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.supplierId = supplierId;
        this.unit = unit;
        this.price = price;
        this.quantity = quantity;

        this.stock = stock;
        this.description = description;
        this.imagePath = imagePath;
        this.dateAdded = dateAdded;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

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

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDate getDateAdded() { return dateAdded; }
    public void setDateAdded(LocalDate dateAdded) { this.dateAdded = dateAdded; }

    // new added
    public int getStock() { return stock; }
    public void setStock(int stock){ this.stock = stock; }

    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description = description; }

    public String getImagePath(){ return imagePath; }
    public void setImagePath(String imagePath){this.imagePath = imagePath; }

    public LocalDate getUpdatdaDate(){ return updatedAt; }
    public void setUpdatedDate( LocalDate updatedAt){ this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return name;
    }
}
