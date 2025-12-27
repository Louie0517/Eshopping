package model;

import java.time.LocalDate;

public class Cart {
  
    private Long cartId;
    private int userId;
    private int prodId;
    private int qty;
    private double subtotal;
    private String prodName;
    private double sellingPrice;
    private String imgPath;
    private LocalDate buyDate;
    
    
    
    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getProdId() { return prodId; }
    public void setProdId(int prodId) { this.prodId = prodId; }
    
    public int getProductId() { return prodId; } 
    public void setProductId(int prodId) { this.prodId = prodId; }
    
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
    
    public String getProdName() { return prodName; }
    public void setProdName(String prodName) { this.prodName = prodName; }
    
    public double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(double sellingPrice) { this.sellingPrice = sellingPrice; }
    
    public String getImgPath() { return imgPath; }
    public void setImgPath(String imgPath) { this.imgPath = imgPath; }
    
    public LocalDate getBuyDate() { return buyDate; }
    public void setBuyDate(LocalDate buyDate) { this.buyDate = buyDate; }

    
    public double getSubtotal() {
        subtotal = sellingPrice * qty;
        return subtotal;
    }

    public void setSubtotal(double subtotal){ this.subtotal = subtotal;}
}

