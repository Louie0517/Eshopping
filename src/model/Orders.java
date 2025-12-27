package model;

import java.time.LocalDateTime;

public class Orders {

    public static final double TAX_RATE = 0.12;
    public static final double SHIPPING_FEE = 50.0;

    
    private int id;
    private int userId;
    private int productId;
    private int quantity;

    private String productName;
    private String brandName;
    private String imagePath;
    private String status;
    private String username;
    private String address;
    private String phoneNumber;
    private String email; 

    private double price; 
    private double total;

    private LocalDateTime orderDate;

    
    public Orders() {}

    public Orders(int userId, int productId, int quantity, double price) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public String getPhoneNumber(){return phoneNumber;}
    public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}

    public String getUsername(){return username;}
    public void setUsername(String username){ this.username = username; }

    public String getAddress(){return address; }
    public void setAddress(String address){this.address = address;}
   
    public double getSubtotal() { return quantity * price;}

    public double getTax() { return getSubtotal() * TAX_RATE; }

    public double getTotal() {
        total = getSubtotal() + getTax() + SHIPPING_FEE;
        return total;
    }

    public void setTotal(double total) {this.total = total;}
}
