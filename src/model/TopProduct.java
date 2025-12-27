package model;

public class TopProduct {
    private long productId;
    private String productName;
    private int totalQuantitySold;
    private double totalRevenue;
    private int timesPurchased;
    
    public TopProduct() {}
    
    public TopProduct(long productId, String productName, int totalQuantitySold, 
                     double totalRevenue, int timesPurchased) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantitySold = totalQuantitySold;
        this.totalRevenue = totalRevenue;
        this.timesPurchased = timesPurchased;
    }
    
    // Getters and Setters
    public long getProductId() {
        return productId;
    }
    
    public void setProductId(long productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public int getTotalQuantitySold() {
        return totalQuantitySold;
    }
    
    public void setTotalQuantitySold(int totalQuantitySold) {
        this.totalQuantitySold = totalQuantitySold;
    }
    
    public double getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public int getTimesPurchased() {
        return timesPurchased;
    }
    
    public void setTimesPurchased(int timesPurchased) {
        this.timesPurchased = timesPurchased;
    }
    
    @Override
    public String toString() {
        return String.format(
            "Product: %s (ID: %d)\nQuantity Sold: %d\nRevenue: $%.2f\nTimes Purchased: %d",
            productName, productId, totalQuantitySold, totalRevenue, timesPurchased
        );
    }
}